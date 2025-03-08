package main

import (
	"bytes"
	"context"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"regexp"
	"strings"

	"github.com/docker/docker/api/types/container"
	"github.com/google/uuid"
)

var basePath = "./code_storage"

func cppHandler(code string, input string) (string, error) {
	ctx := context.Background()

	id := uuid.New().String()
	testFolder := filepath.Join(basePath, "test")

	if err := os.MkdirAll(testFolder, os.ModePerm); err != nil {
		return "", fmt.Errorf("failed to create test directory: %w", err)
	}

	codeFile, err := filepath.Abs(filepath.Join(testFolder, id+"_code.cpp"))
	if err != nil {
		return "", fmt.Errorf("failed to get absolute path for code file: %w", err)
	}

	inputFile, err := filepath.Abs(filepath.Join(testFolder, id+"_input.txt"))
	if err != nil {
		return "", fmt.Errorf("failed to get absolute path for input file: %w", err)
	}

	if err := os.WriteFile(codeFile, []byte(code), 0644); err != nil {
		return "", fmt.Errorf("failed to write code file: %w", err)
	}
	if err := os.WriteFile(inputFile, []byte(input), 0644); err != nil {
		return "", fmt.Errorf("failed to write input file: %w", err)
	}

	containerName := "container_" + id
	compileCmd := fmt.Sprintf("g++ -o program %s", filepath.Base(codeFile))
	runCmd := "timeout -s KILL 1 ./program < input.txt"

	resp, err := cli.ContainerCreate(ctx, &container.Config{
		Image:      "gcc:latest",
		Cmd:        []string{"/bin/sh", "-c", compileCmd + " && " + runCmd},
		WorkingDir: "/usr/src/app",
		Env:        []string{"LC_ALL=C.UTF-8", "LANG=C.UTF-8"},
	}, &container.HostConfig{
		Binds: []string{
			codeFile + ":/usr/src/app/" + filepath.Base(codeFile),
			inputFile + ":/usr/src/app/input.txt",
		},
		Resources: container.Resources{
			Memory:   256 * 1024 * 1024,
			NanoCPUs: 500000000,
		},
	}, nil, nil, containerName)
	if err != nil {
		return "", fmt.Errorf("failed to create container: %w", err)
	}

	if err := cli.ContainerStart(ctx, resp.ID, container.StartOptions{}); err != nil {
		return "", fmt.Errorf("failed to start container: %w", err)
	}

	statusCh, errCh := cli.ContainerWait(ctx, resp.ID, container.WaitConditionNotRunning)
	select {
	case <-statusCh:
	case err := <-errCh:
		return "", fmt.Errorf("error waiting for container: %w", err)
	}

	//error  stderr logs
	stderrLogs, err := cli.ContainerLogs(ctx, resp.ID, container.LogsOptions{ShowStderr: true})
	if err != nil {
		return "", fmt.Errorf("failed to get stderr logs: %w", err)
	}
	var stderrBuf bytes.Buffer
	_, _ = io.Copy(&stderrBuf, stderrLogs)
	stderr := stderrBuf.String()

	// Read stdout logs
	stdoutLogs, err := cli.ContainerLogs(ctx, resp.ID, container.LogsOptions{ShowStdout: true})
	if err != nil {
		return "", fmt.Errorf("failed to get stdout logs: %w", err)
	}
	var stdoutBuf bytes.Buffer
	_, _ = io.Copy(&stdoutBuf, stdoutLogs)
	stdout := stdoutBuf.String()

	// Cleanup container and files
	go func() {
		defer os.Remove(codeFile)
		defer os.Remove(inputFile)

		err := cli.ContainerRemove(ctx, resp.ID, container.RemoveOptions{Force: true})
		if err != nil {
			fmt.Printf("failed to remove container: %v\n", err)
		}
	}()

	// If stderr is not empty, return it as an error
	if len(stderr) > 0 {
		return "", fmt.Errorf("execution error: %s", cleanOutput(stderr))
	}

	return cleanOutput(stdout), nil
}

func cleanOutput(log string) string {

	re := regexp.MustCompile(`[\x00-\x1F\x7F]`)
	return strings.TrimSpace(re.ReplaceAllString(log, ""))
}
