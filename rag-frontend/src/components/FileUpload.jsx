
import { useRef, useState } from "react";
import axios from "axios";
import { UploadCloud, FileText, FileSpreadsheet } from "lucide-react";

function FileUpload({ onUploadSuccess }) {

  const fileInput = useRef(null);

  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleFileChange = (e) => {

    const selectedFile = e.target.files[0];

    if (!selectedFile) return;

    const allowedExtensions = /\.(pdf|xls|xlsx)$/i;

    if (!allowedExtensions.test(selectedFile.name)) {
      setMessage("Only PDF and Excel files are allowed.");
      setFile(null);
      return;
    }

    setMessage("");
    setFile(selectedFile);
  };

  const handleUpload = async () => {

    if (!file) {
      setMessage("Please select a document first.");
      return;
    }

    try {

      setLoading(true);
      setMessage("");

      const formData = new FormData();
      formData.append("file", file);

      const response = await axios.post(
        "http://localhost:8082/api/documents/upload",
        formData
      );

      const result = response.data;

      setMessage(result.message || "Upload successful!");

      onUploadSuccess?.({
        ...result,
        fileName: result.fileName || file.name
      });

      setFile(null);
      fileInput.current.value = "";

    } catch (error) {

      console.error("Upload error:", error);

      setMessage(
        error.response?.data?.message || "Document upload failed."
      );

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-card">

      <div className="upload-heading">
        <div>
          <h2>Upload Documents</h2>
          <p>Upload PDF or Excel files to your knowledge base.</p>
        </div>
        <UploadCloud size={28} />
      </div>

      <div
        className="drop-zone"
        onClick={() => fileInput.current.click()}
      >
        <UploadCloud size={42} />
        <h3>Drop your files here</h3>
        <p>or click to browse files</p>
        <span>PDF, XLS, XLSX supported</span>

        <input
          ref={fileInput}
          type="file"
          accept=".pdf,.xls,.xlsx"
          hidden
          onChange={handleFileChange}
        />
      </div>

      {file && (
        <div className="selected-file">

          {file.name.endsWith(".pdf")
            ? <FileText />
            : <FileSpreadsheet />
          }

          <span>{file.name}</span>
          <span>{(file.size / 1024).toFixed(1)} KB</span>

        </div>
      )}

      <button
        className="upload-btn"
        onClick={handleUpload}
        disabled={!file || loading}
      >
        {loading ? "Uploading..." : "Upload Document"}
      </button>

      {message && (
        <p className="upload-message">{message}</p>
      )}

    </div>
  );
}

export default FileUpload;
