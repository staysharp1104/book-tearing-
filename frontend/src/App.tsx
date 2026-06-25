import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { ChatProvider } from './context/ChatContext';
import BookLibrary from './pages/BookLibrary';
import BookDetail from './pages/BookDetail';
import BookAnalysisPage from './pages/BookAnalysisPage';
import PromptTemplateManage from './pages/PromptTemplateManage';

const App: React.FC = () => {
  return (
    <ConfigProvider locale={zhCN}>
      <ChatProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<BookLibrary />} />
            <Route path="/books/:bookId" element={<BookDetail />} />
            <Route path="/books/:bookId/analysis" element={<BookAnalysisPage />} />
            <Route path="/admin/prompts" element={<PromptTemplateManage />} />
          </Routes>
        </BrowserRouter>
      </ChatProvider>
    </ConfigProvider>
  );
};

export default App;
