import React from 'react';

const InputOutputBoxes = ({ darkMode, input, setInput, output, setOutput, Error ,setError}) => {

  const handleClearInput = () => {
    console.log('Clearing input');
    setInput('');
  };

  const handleClearOutput = () => {
    console.log('Clearing output');
    setError(false)
    setOutput('');
  };

  const handleInputChange = (e) => {
    const newValue = e.target.value;
    console.log('Input text changed:', newValue);
    setInput(newValue);
  };

  return (
    <div className="grid grid-rows-2 gap-0">
      {/* Input Box */}
      <div className={`p-4 rounded-lg shadow ${darkMode ? 'bg-[#272822] border border-gray-200' : 'bg-[#f8f8f2] border border-gray-800'}`}>
        <div className="flex items-center justify-between mb-4">
          <h2 className={`text-lg font-semibold ${darkMode ? 'text-[#f8f8f2]' : 'text-black'}`}>Input</h2>
          <button
            className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600"
            onClick={handleClearInput}
          >
            Clear
          </button>
        </div>
        <textarea
          className={`w-full h-48 p-2 border rounded ${darkMode ? 'bg-[#1e1e1e] text-[#f8f8f2] border-gray-600' : 'bg-white text-black border-gray-300'}`}
          value={input}
          onChange={handleInputChange}
          placeholder="Enter input here..."
        />
      </div>

      {/* Output Box */}
      <div className={`p-4 rounded-lg shadow ${darkMode ? `bg-[#272822] border ${Error ? 'border-red-500' : 'border-gray-400'}` : `bg-[#f8f8f2] border ${Error ? 'border-red-500' : 'border-gray-800'}`} mt-0`}>
        <div className="flex items-center justify-between mb-4">
          <h2 className={`text-lg font-semibold ${darkMode ? 'text-[#f8f8f2]' : 'text-black'}`}>Output</h2>
          <button
            className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600"
            onClick={handleClearOutput}
          >
            Clear
          </button>
        </div>
        <textarea
          className={`w-full h-48 p-2 border rounded ${darkMode ? 'bg-[#1e1e1e] text-[#f8f8f2] border-gray-600' : 'bg-white text-black border-gray-300'}`}
          value={output}
          readOnly
          placeholder="Output will be displayed here..."
        />
      </div>
    </div>
  );
};

export default InputOutputBoxes;
