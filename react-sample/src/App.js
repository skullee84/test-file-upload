import './App.css';
import React, {useState, useMemo} from 'react';
import {useDropzone} from 'react-dropzone'
import {post} from 'axios';
import { Line } from 'rc-progress';

const baseStyle = {
  flex: 1,
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  padding: '20px',
  borderWidth: 2,
  borderRadius: 2,
  borderColor: '#eeeeee',
  borderStyle: 'dashed',
  backgroundColor: '#fafafa',
  color: '#bdbdbd',
  outline: 'none',
  transition: 'border .24s ease-in-out'
};

const focusedStyle = {
  borderColor: '#2196f3'
};

const acceptStyle = {
  borderColor: '#00e676'
};

const rejectStyle = {
  borderColor: '#ff1744'
};

function StyledDropzone(props) {
  const {
    acceptedFiles,
    getRootProps,
    getInputProps,
    isFocused,
    isDragAccept,
    isDragReject
  } = useDropzone();

  const [state, setState] = useState({
    percent: 0
  });

  const files = acceptedFiles.map(file => {
    return (
      <li key={file.path}>
        {file.path} - {file.size} bytes
      </li>
    );
  });

  const style = useMemo(() => ({
    ...baseStyle,
    ...(isFocused ? focusedStyle : {}),
    ...(isDragAccept ? acceptStyle : {}),
    ...(isDragReject ? rejectStyle : {})
  }), [
    isFocused,
    isDragAccept,
    isDragReject
  ]);

  const uploadClick = () => {
    console.log(acceptedFiles);

    setState({ percent: 0 });
    let data = new FormData();
    acceptedFiles.forEach(file => {
      data.append('files[]', file, file.name);
    });

    const config = {
      headers: { 'content-type': 'multipart/form-data' },
      onUploadProgress: progressEvent => {
        var percent = Math.round(progressEvent.loaded * 100 / progressEvent.total);
        if (percent >= 100) {
          setState({ percent: 100 });
        } else {
          setState({ percent });
        }
      }
    };

    post('http://localhost/api/upload', data, config)
      .then(function(response) {
        setState({ percent: 0 });
      })
      .catch(function(error) {
        console.log(error);
        setState({ percent: 0 });
      });
  };

  return (
    <div className="container">
      <p>{state.percent}</p>
      <Line percent={state.percent} strokeWidth={2} strokeColor="#3FC7FA" />
      <div {...getRootProps({style})}>
        <input {...getInputProps()} />
        <p>Drag 'n' drop some files here, or click to select files</p>
        <aside>
          <ul>{files}</ul>
        </aside>
      </div>
      <button onClick={uploadClick}>upload</button>
    </div>
  );
}

function App() {
  return (
    <div className="App">
      <header className="App-header">
        File Upload...
        <StyledDropzone />
      </header>
    </div>
  );
}

export default App;
