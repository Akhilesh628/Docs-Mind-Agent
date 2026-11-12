
import { useState, useEffect, useRef } from "react";
import axios from "axios";

import {
  Send,
  Bot,
  User,
  LoaderCircle,
  Sparkles,
  Search,
  GitCompare,
  Lightbulb
} from "lucide-react";

import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

function ChatPanel() {
  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);

  const messagesEndRef = useRef(null);

  // Suggested questions
  const suggestions = [
    {
      label: "Summarize Documents",
      question: "Summarize my uploaded documents",
      icon: Sparkles
    },
    {
      label: "Find Key Points",
      question: "Find the important key points from my documents",
      icon: Search
    },
    {
      label: "Compare Documents",
      question: "Compare the information in my uploaded documents",
      icon: GitCompare
    },
    {
      label: "Explain Topics",
      question: "Explain the main topics covered in my documents",
      icon: Lightbulb
    }
  ];

  // Automatically scroll to latest message
  useEffect(() => {
    const chatContainer = messagesEndRef.current;

    if (chatContainer) {
      chatContainer.scrollTo({
        top: chatContainer.scrollHeight,
        behavior: "smooth"
      });
    }
  }, [messages, loading]);

  // Handle suggestion click
  const handleSuggestion = (suggestedQuestion) => {
    setQuestion(suggestedQuestion);
  };

  // Ask AI question
  const askQuestion = async () => {
    if (!question.trim() || loading) return;

    const userQuestion = question.trim();

    setMessages((prev) => [
      ...prev,
      { role: "user", content: userQuestion }
    ]);

    setQuestion("");
    setLoading(true);

    try {
      const params = new URLSearchParams();
      params.append("question", userQuestion);

      const response = await axios.post(
        "http://localhost:8082/api/rag/ask",
        params
      );

      const answer =
        typeof response.data === "string"
          ? response.data
          : response.data.answer || JSON.stringify(response.data);

      setMessages((prev) => [
        ...prev,
        { role: "assistant", content: answer }
      ]);

    } catch (error) {
      console.error("RAG API Error:", error);

      setMessages((prev) => [
        ...prev,
        {
          role: "assistant",
          content:
            "Unable to connect to the RAG backend. Please check whether Spring Boot is running."
        }
      ]);

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="chat-panel">

      {/* Chat Messages */}
      <div className="chat-messages" ref={messagesEndRef}>

        {/* Empty Chat Screen */}
        {messages.length === 0 && (
          <div className="chat-empty">

            <div className="chat-empty-icon">
              <Bot size={40} />
            </div>

            <h3>Ask Anything to Atharva AI</h3>

            <p>
              Explore your knowledge with ATHARVA AI.
              Summarize documents, discover important insights,
              compare information, and get intelligent answers.
            </p>

            {/* Suggested Questions */}
            <div className="suggested-prompts">

              {suggestions.map((item, index) => {
                const Icon = item.icon;

                return (
                  <button
                    key={index}
                    className="suggestion-btn"
                    onClick={() =>
                      handleSuggestion(item.question)
                    }
                    disabled={loading}
                  >
                    <Icon size={16} />
                    <span>{item.label}</span>
                  </button>
                );
              })}

            </div>
          </div>
        )}

        {/* Chat Messages */}
        {messages.map((message, index) => (
          <div
            key={index}
            className={`message ${
              message.role === "user" ? "user" : "assistant"
            }`}
          >

            <div className="message-avatar">
              {message.role === "user" ? (
                <User size={18} />
              ) : (
                <Bot size={18} />
              )}
            </div>

            <div className="message-content">

              <div className="message-role">
                {message.role === "user"
                  ? "You"
                  : "ATHARVA AI"}
              </div>

              {message.role === "assistant" ? (
                <div className="markdown-answer">
                  <ReactMarkdown remarkPlugins={[remarkGfm]}>
                    {message.content}
                  </ReactMarkdown>
                </div>
              ) : (
                <p>{message.content}</p>
              )}

            </div>
          </div>
        ))}

        {/* AI Loading Indicator */}
        {loading && (
          <div className="message assistant">

            <div className="message-avatar">
              <Bot size={18} />
            </div>

            <div className="message-content loading-chat">
              <LoaderCircle
                className="loading-icon"
                size={20}
              />
              <span>ATHARVA AI is thinking...</span>
            </div>

          </div>
        )}

      </div>

      {/* Chat Input */}
      <div className="chat-input-area">

        <input
          type="text"
          placeholder="Ask anything about your documents..."
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") askQuestion();
          }}
        />

        <button
          onClick={askQuestion}
          disabled={loading || !question.trim()}
        >
          <Send size={18} />
        </button>

      </div>

      <div className="chat-footer">
        ATHARVA AI powered by 
      </div>

    </div>
  );
}

export default ChatPanel;
