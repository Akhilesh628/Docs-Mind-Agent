
import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8082/api",
  timeout: 60000
});

// Upload PDF / Excel
export const uploadDocument = async (file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await API.post(
    "/documents/upload",
    formData
  );

  return response.data;
};

// Ask RAG question
export const askQuestion = async (question) => {
  const params = new URLSearchParams();
  params.append("question", question);

  const response = await API.post(
    "/rag/ask",
    params
  );

  return response.data;
};

export default API;
