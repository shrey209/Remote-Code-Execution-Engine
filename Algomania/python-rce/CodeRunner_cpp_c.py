import os
import uuid
import docker

def create_and_run_cpp_container(code_file, input_file):
    client = docker.from_env()
    code_file_path = os.path.abspath(code_file)
    input_file_path = os.path.abspath(input_file)
    code_dir = os.path.dirname(code_file_path)

    base_image = 'gcc:latest'
    compile_command = f'g++ -o program {os.path.basename(code_file)}'
    run_command = f'timeout -s KILL 1 ./program' 

    container_name = f"container_{str(uuid.uuid4())}"
    container = client.containers.run(
        image=base_image,
        command=f" /bin/sh -c '{compile_command} && {run_command} < input.txt'",
        volumes={
            code_dir: {'bind': '/usr/src/app', 'mode': 'rw'},
            input_file_path: {'bind': '/usr/src/app/input.txt', 'mode': 'rw'}
        },
        working_dir='/usr/src/app',
        detach=True,
        stdin_open=True,
        name=container_name
    )

    # blocking to finish the containers waiting
    container.wait()

    output = container.logs(stdout=True, stderr=False).decode('utf-8')
    error = container.logs(stdout=False, stderr=True).decode('utf-8')

    # destroying containers
    container.remove()

    return output, error
