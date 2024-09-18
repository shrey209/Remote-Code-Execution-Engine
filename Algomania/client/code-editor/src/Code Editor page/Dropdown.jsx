import React from 'react';
import { IoIosArrowDropdownCircle } from 'react-icons/io';

const Dropdown = ({ lang, setLang }) => {
  const handleChange = (e) => {
    console.log(e.target.value); 
    setLang(e.target.value); 
  };

  return (
    <div className="relative inline-block">
      <select
        className="block appearance-none bg-white border border-gray-300 text-gray-700 py-1 px-2 pr-6 rounded leading-tight focus:outline-none focus:border-purple-500 focus:bg-white"
        value={lang} 
        onChange={handleChange} 
      >
        <option value="cpp">C++</option>
        <option value="python">Python</option>
      </select>
      <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2 text-gray-700">
        <IoIosArrowDropdownCircle className="h-4 w-4" />
      </div>
    </div>
  );
};

export default Dropdown;
