import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button, Spin, message } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import type { AnalysisPageData, PromptTemplate, ChatHistory } from '../types';
import { fetchAnalysisPage } from '../api/analysis';
import { streamChat } from '../api/chat';
import { useChat } from '../context/ChatContext';
import BookInfoSection from '../components/analysis/BookInfoSection';
import ChapterContentSection from '../components/analysis/ChapterContentSection';
import PromptToolbar from '../components/prompt/PromptToolbar';
import PromptManageModal from '../components/prompt/PromptManageModal';
import ChatSection from '../components/chat/ChatSection';

const BookAnalysisPage: React.FC = () => {
  const { bookId } = useParams<{ bookId: string }>();
  const navigate = useNavigate();
  const { messages, addMessage, updateLastMessage, setSources, setStreaming, clearMessages, isStreaming } = useChat();

  const [data, setData] = useState<AnalysisPageData | null>(null);
  const [loading, setLoading] = useState(true);
  const [promptModalOpen, setPromptModalOpen] = useState(false);
  const [activePromptId, setActivePromptId] = useState<number | undefined>(undefined);

  useEffect(() => {
    if (!bookId) return;
    setLoading(true);
    clearMessages();
    fetchAnalysisPage(bookId)
      .then(setData)
      .catch(() => message.error('加载分析页面失败'))
      .finally(() => setLoading(false));
  }, [bookId]);

  const handleSendMessage = async (msg: string, promptTemplateId?: number) => {
    if (!bookId) return;

    // Add user message
    addMessage({
      id: Date.now(),
      bookId,
      role: 'user',
      content: msg,
      source: 'fanqie',
      createTime: new Date().toISOString(),
    });

    // Add placeholder assistant message
    const assistantId = Date.now() + 1;
    addMessage({
      id: assistantId,
      bookId,
      role: 'assistant',
      content: '',
      source: 'fanqie',
      createTime: new Date().toISOString(),
    });

    setStreaming(true);

    // messages.length 是当前闭包值（尚未包含新添加的 2 条消息）
    // 助手消息在新 state 中的索引 = 当前长度 + 1
    const msgIndex = messages.length + 1;

    await streamChat(
      {
        bookId,
        message: msg,
        promptTemplateId: promptTemplateId || activePromptId,
        tenChapterContext: true,
      },
      (token) => {
        updateLastMessage(token);
      },
      (srcs) => {
        setSources(msgIndex, srcs);
      },
      () => {
        setStreaming(false);
      },
      (error) => {
        message.error(error);
        setStreaming(false);
      }
    );
  };

  const handleSelectPrompt = (prompt: PromptTemplate) => {
    setActivePromptId(prompt.id);
    handleSendMessage(`请使用「${prompt.name}」模板分析：`, prompt.id);
  };

  if (loading) return <div style={{ textAlign: 'center', padding: 60 }}><Spin size="large" /></div>;
  if (!data) return <div style={{ textAlign: 'center', padding: 60, color: '#aaa' }}>加载失败</div>;

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(`/books/${bookId}`)} style={{ marginBottom: 16 }}>返回</Button>

      {/* Book Info */}
      <div style={{ marginBottom: 16 }}>
        <BookInfoSection
          title={data.book.title}
          author={data.book.author}
          source={data.book.source}
          intro={data.book.intro}
          coverUrl={data.book.coverUrl}
        />
      </div>

      {/* Prompt Toolbar */}
      <PromptToolbar
        prompts={data.quickPrompts}
        onSelect={handleSelectPrompt}
        onManage={() => setPromptModalOpen(true)}
      />

      {/* Two column layout: Chapters + Chat */}
      <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 280px)' }}>
        {/* Left: Chapter content */}
        <div style={{ flex: 1, minWidth: 0 }}>
          <ChapterContentSection chapters={data.chapters} />
        </div>

        {/* Right: AI Chat */}
        <div style={{ width: 420, flexShrink: 0, display: 'flex', flexDirection: 'column' }}>
          <ChatSection bookId={bookId!} contextMode onSendMessage={handleSendMessage} />
        </div>
      </div>

      {/* Prompt Manage Modal */}
      <PromptManageModal
        open={promptModalOpen}
        onClose={() => setPromptModalOpen(false)}
        onSaved={() => {
          // Refresh quick prompts
          if (bookId) fetchAnalysisPage(bookId).then(setData);
        }}
      />
    </div>
  );
};

export default BookAnalysisPage;
