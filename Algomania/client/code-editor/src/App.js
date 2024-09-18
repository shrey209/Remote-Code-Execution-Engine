import React, { useEffect } from 'react';
import Editor from "./Code Editor page/Editor";

function App() {
  useEffect(() => {
    document.title = 'Algomania'; 
  }, []); 

  return (
    <>
      <Editor />
    </>
  );
}

export default App;
