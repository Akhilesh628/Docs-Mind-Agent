
import { useState } from "react";

import Sidebar from "./components/Sidebar";
import FileUpload from "./components/FileUpload";
import ChatPanel from "./components/ChatPanel";

import {
  FileText,
  MessageSquare,
  Database
} from "lucide-react";

import "./App.css";

function App() {
  const [activePage, setActivePage] = useState("Dashboard");
  const [documents, setDocuments] = useState([]);
  const [backendOnline, setBackendOnline] = useState(false);

  // Called after successful upload
  const handleUploadSuccess = (result) => {
    setBackendOnline(true);

    setDocuments((prev) => [
      {
        id: Date.now(),
        fileName: result.fileName,
        totalChunks: result.totalChunks
      },
      ...prev
    ]);
  };

  const handleNavigation = (page) => {
    setActivePage(page);

    const sectionId = {
      Dashboard: "dashboard",
      "AI Chat": "chat-section",
      Documents: "documents-section"
    }[page];

    document.getElementById(sectionId)?.scrollIntoView({
      behavior: "smooth",
      block: "start"
    });
  };

  return (
    <div className="app-layout">

      <Sidebar
        activePage={activePage}
        setActivePage={handleNavigation}
      />

      <main className="main-content" id="dashboard">

        <header className="top-header">
        </header>

        {/* Welcome Section */}
        <section className="welcome-section">

          <div>
            <span className="welcome-tag">
              YOUR PERSONAL AI ASSISTANT
            </span>

            <h2>Welcome to your Atharva AI</h2>

            <p>
              Where information connects, knowledge evolves, and intelligence
              takes action. Discover a smarter way to explore, understand,
              and unlock the potential of your data.
            </p>
          </div>

          <div className="welcome-icon">
            <Database size={65} />
          </div>

        </section>


        {/* Main Dashboard */}
        <section className="dashboard-grid">

          {/* Document Upload */}
          <div className="left-column" id="documents-section">

            <FileUpload
              onUploadSuccess={handleUploadSuccess}
            />

          </div>

          {/* AI Chat */}
          <div className="right-column" id="chat-section">

            <ChatPanel />

          </div>

        </section>

        <footer className="dashboard-footer">
          RAG Assistant · Spring AI · Groq · PostgreSQL PGVector
        </footer>

      </main>

    </div>
  );
}

export default App;
