import os
import uuid
import docker






def create_and_run_python_container(code_file, input_file, output_file, error_file, timeout=3):
    client = docker.from_env()
    code_file_path = os.path.abspath(code_file)
    input_file_path = os.path.abspath(input_file)
    code_dir = os.path.dirname(code_file_path)

    base_image = 'python:latest'
    run_command = f'timeout {timeout}s python {os.path.basename(code_file)} < input.txt'  # Adding timeout to the run command

    container_name = f"container_{str(uuid.uuid4())}"

    container = client.containers.run(
        image=base_image,
        command=f"/bin/sh -c '{run_command}'",
        volumes={
            code_dir: {'bind': '/usr/src/app', 'mode': 'rw'},
            input_file_path: {'bind': '/usr/src/app/input.txt', 'mode': 'rw'}
        },
        working_dir='/usr/src/app',
        detach=True,
        stdin_open=True,
        name=container_name
    )

    # blocing
    container.wait()


    output = container.logs(stdout=True, stderr=False).decode('utf-8')
    error = container.logs(stdout=False, stderr=True).decode('utf-8')

  
    with open(output_file, 'w') as f:
        f.write(output)
    with open(error_file, 'w') as f:
        f.write(error)

    # destroying the contianers
    container.remove()

    return output, error