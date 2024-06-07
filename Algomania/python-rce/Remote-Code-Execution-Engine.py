import os
from fastapi import FastAPI
from pydantic import BaseModel
import uuid

from CodeRunner_cpp_c import create_and_run_cpp_container
from CodeRunner_python import create_and_run_python_container




app = FastAPI()

class CodeInput(BaseModel):
    code: str
    input_data: str
    lang: str

class ResponseDTO(BaseModel):
    isError: bool
    response: str

@app.post("/code", response_model=ResponseDTO)
async def code_runner(code_input: CodeInput):
    random_uuid = str(uuid.uuid4())

    base_dir = os.path.dirname(os.path.abspath(__file__)) 

    code_extension = "cpp" if code_input.lang in ["cpp", "c"] else "py"
    code_filename = f"{random_uuid}.{code_extension}"
    input_filename = f"{random_uuid}.txt"
    output_filename = f"{random_uuid}_output.txt"
    error_filename = f"{random_uuid}_error.txt"
    
    code_file_path = os.path.join(base_dir, code_filename)
    input_file_path = os.path.join(base_dir, input_filename)
    output_file_path = os.path.join(base_dir, output_filename)
    error_file_path = os.path.join(base_dir, error_filename)

 
    with open(code_file_path, 'w') as code_file:   
        code_file.write(code_input.code)

   
    with open(input_file_path, 'w') as input_file:    
        input_file.write(code_input.input_data)

  
    if code_input.lang in ["cpp"]:
        output, error = create_and_run_cpp_container(code_file_path, input_file_path, output_file_path, error_file_path)
    elif code_input.lang == "python":
        output, error = create_and_run_python_container(code_file_path, input_file_path, output_file_path, error_file_path)   
    else:
        return ResponseDTO(isError=True, response="Unsupported language specified")

   
    is_error = bool(error.strip()) 


    response_dto = ResponseDTO(
        isError=is_error,
        response=error if is_error else output
    )

    # del all fiels
    os.remove(code_file_path)
    os.remove(input_file_path)
    os.remove(output_file_path)
    os.remove(error_file_path)

    return response_dto


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8001)
