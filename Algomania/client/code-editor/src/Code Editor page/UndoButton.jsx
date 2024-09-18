import React from 'react';
import { FaUndo } from 'react-icons/fa';

const UndoButton = ({ onClick }) => {
  return (
    <button
      onClick={onClick}
      className="flex items-center bg-blue-500 hover:bg-blue-600 text-white px-3 py-2 rounded shadow"
    >
      <FaUndo className="mr-2" />
      Clear
    </button>
  );
};

export default UndoButton;
