import os
import uuid
import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from CodeRunner_cpp_c import create_and_run_cpp_container
from CodeRunner_python import create_and_run_python_container

# -------------------- FastAPI App Setup --------------------

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  
    allow_credentials=True,
    allow_methods=["*"],  
    allow_headers=["*"], 
)

# -------------------- Data Models --------------------

class CodeInput(BaseModel):
    code: str
    input_data: str
    lang: str

class ResponseDTO(BaseModel):
    isError: bool
    response: str

# -------------------- Code Execution Endpoint --------------------

@app.post("/code", response_model=ResponseDTO)
async def code_runner(code_input: CodeInput):
    # Generate unique identifiers for file names
    random_uuid = str(uuid.uuid4())
    base_dir = os.path.dirname(os.path.abspath(__file__))

    # Determine file extensions based on language
    code_extension = "cpp" if code_input.lang in ["cpp", "c"] else "py"
    code_filename = f"{random_uuid}.{code_extension}"
    input_filename = f"{random_uuid}.txt"
   
    code_file_path = os.path.join(base_dir, code_filename)
    input_file_path = os.path.join(base_dir, input_filename)

    # Write code and input to files
    with open(code_file_path, 'w') as code_file:   
        code_file.write(code_input.code)

    with open(input_file_path, 'w') as input_file:    
        input_file.write(code_input.input_data)

    # Execute code in a container
    if code_input.lang == "cpp":
        output, error = create_and_run_cpp_container(code_file_path, input_file_path)
    elif code_input.lang == "python":
        output, error = create_and_run_python_container(code_file_path, input_file_path)
    else:
        return ResponseDTO(isError=True, response="Unsupported language specified")

    # Check for errors
    is_error = bool(error.strip())

    response_dto = ResponseDTO(
        isError=is_error,
        response=error if is_error else output
    )

    # Clean up files
    os.remove(code_file_path)
    os.remove(input_file_path)

    return response_dto

# -------------------- Main Method --------------------

def main():
    uvicorn.run(app, host="0.0.0.0", port=8000)

if __name__ == "__main__":
    main()
