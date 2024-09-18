import React from 'react';
import AceEditor from 'react-ace';
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-python';
import 'ace-builds/src-noconflict/theme-monokai'; // Dark theme
import 'ace-builds/src-noconflict/theme-chrome'; // Light theme

const EditorComponent = ({ code, setCode, lang, darkMode }) => {
  const theme = darkMode ? 'monokai' : 'chrome'; // Conditionally select theme based on darkMode

  const handleChange = (newValue) => {
    console.log('Code changed:', newValue);
    setCode(newValue);
  };

  return (
    <div className="bg-white shadow-lg rounded-lg">
      <AceEditor
        mode={lang === 'cpp' ? 'c_cpp' : 'python'}
        theme={theme}
        fontSize={14}
        showPrintMargin={true}
        showGutter={true}
        highlightActiveLine={true}
        width="100%"
        height="500px"
        value={code}
        onChange={handleChange}
        placeholder={`Write your ${lang === 'cpp' ? 'C++' : 'Python'} code here...`}
        setOptions={{
          enableBasicAutocompletion: true,
          enableLiveAutocompletion: true,
          enableSnippets: true,
          showLineNumbers: true,
          tabSize: 2,
        }}
        editorProps={{ $blockScrolling: Infinity }}
      />
    </div>
  );
};

export default EditorComponent;
