import React from 'react';
import { BsBrightnessHigh } from 'react-icons/bs';
import { MdDarkMode } from 'react-icons/md';

const Darkmodebutton = ({ darkMode, setDarkMode }) => {
  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
  };

  return (
    <div className="flex items-center">
      <button
        onClick={toggleDarkMode}
        className="bg-gray-200 text-gray-600 dark:bg-gray-700 dark:text-gray-300 rounded-full p-2 focus:outline-none"
        style={{ width: '36px', height: '36px' }} // Adjust size here
      >
        {darkMode ? <MdDarkMode size={20} /> : <BsBrightnessHigh size={20} />} {/* Adjust icon size here */}
      </button>
      <span className="ml-2 text-gray-700 dark:text-gray-300">{darkMode ? 'Dark Mode' : 'Light Mode'}</span>
    </div>
  );
};

export default Darkmodebutton;
