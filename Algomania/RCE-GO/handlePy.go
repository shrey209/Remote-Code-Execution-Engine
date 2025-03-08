package main

import (
	"bytes"
	"context"
	"fmt"
	"io"
	"os"
	"path/filepath"

	"github.com/docker/docker/api/types/container"
	"github.com/google/uuid"
)

func pythonHandler(code string, input string) (string, error) {
	ctx := context.Background()

	id := uuid.New().String()
	testFolder := filepath.Join(basePath, "test")

	// Ensure test directory exists
	if err := os.MkdirAll(testFolder, os.ModePerm); err != nil {
		return "", fmt.Errorf("failed to create test directory: %w", err)
	}

	// Create file paths
	codeFile, err := filepath.Abs(filepath.Join(testFolder, id+"_code.py"))
	if err != nil {
		return "", fmt.Errorf("failed to get absolute path for code file: %w", err)
	}

	inputFile, err := filepath.Abs(filepath.Join(testFolder, id+"_input.txt"))
	if err != nil {
		return "", fmt.Errorf("failed to get absolute path for input file: %w", err)
	}

	// Write code and input to files
	if err := os.WriteFile(codeFile, []byte(code), 0644); err != nil {
		return "", fmt.Errorf("failed to write code file: %w", err)
	}
	if err := os.WriteFile(inputFile, []byte(input), 0644); err != nil {
		return "", fmt.Errorf("failed to write input file: %w", err)
	}

	containerName := "container_" + id
	runCmd := "timeout -s KILL 1 python3 script.py < input.txt"

	// Create and start Docker container
	resp, err := cli.ContainerCreate(ctx, &container.Config{
		Image:      "python:latest",
		Cmd:        []string{"/bin/sh", "-c", runCmd},
		WorkingDir: "/usr/src/app",
	}, &container.HostConfig{
		Binds: []string{
			codeFile + ":/usr/src/app/script.py",
			inputFile + ":/usr/src/app/input.txt",
		},
		Resources: container.Resources{
			Memory:   256 * 1024 * 1024, // 256MB RAM
			NanoCPUs: 500000000,         // 0.5 CPU
		},
	}, nil, nil, containerName)
	if err != nil {
		return "", fmt.Errorf("failed to create container: %w", err)
	}

	if err := cli.ContainerStart(ctx, resp.ID, container.StartOptions{}); err != nil {
		return "", fmt.Errorf("failed to start container: %w", err)
	}

	// Wait for container to stop
	statusCh, errCh := cli.ContainerWait(ctx, resp.ID, container.WaitConditionNotRunning)
	select {
	case <-statusCh:
	case err := <-errCh:
		return "", fmt.Errorf("error waiting for container: %w", err)
	}

	// Fetch logs
	logs, err := cli.ContainerLogs(ctx, resp.ID, container.LogsOptions{ShowStdout: true, ShowStderr: true})
	if err != nil {
		return "", fmt.Errorf("failed to get container logs: %w", err)
	}

	var outputBuf bytes.Buffer
	logData, err := io.ReadAll(logs)
	if err != nil {
		return "", fmt.Errorf("failed to read logs: %w", err)
	}

	// Docker log stream format: first byte indicates stream (1=stdout, 2=stderr), next 7 bytes are message length
	if len(logData) > 8 {
		logData = logData[8:] // Remove first 8 bytes
	}

	outputBuf.Write(logData)

	go func() {
		defer os.Remove(codeFile)  // Delete the code file
		defer os.Remove(inputFile) // Delete the input file

		err := cli.ContainerRemove(ctx, resp.ID, container.RemoveOptions{Force: true})
		if err != nil {
			fmt.Printf("failed to remove container: %v\n", err)
		}
	}()

	return outputBuf.String(), nil
}
