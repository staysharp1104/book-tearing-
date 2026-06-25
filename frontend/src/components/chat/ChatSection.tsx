import React from 'react';
import { Input, Button, Spin } from 'antd';
import { SendOutlined } from '@ant-design/icons';
import { useChat } from '../../context/ChatContext';
import SourceReference from './SourceReference';

interface Props {
  bookId: string;
  contextMode?: boolean;
  onSendMessage: (message: string, promptTemplateId?: number) => void;
}

const ChatSection: React.FC<Props> = ({ bookId, contextMode, onSendMessage }) => {
  const { messages, sources, isStreaming } = useChat();
  const [input, setInput] = React.useState('');

  const handleSend = () => {
    if (!input.trim() || isStreaming) return;
    onSendMessage(input.trim());
    setInput('');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      {/* Messages area */}
      <div style={{ flex: 1, overflow: 'auto', padding: 12, background: '#f9f9f9', borderRadius: 8, marginBottom: 12 }}>
        {messages.length === 0 && (
          <div style={{ textAlign: 'center', color: '#aaa', padding: 40 }}>
            输入问题开始AI书籍分析
          </div>
        )}
        {messages.map((msg, idx) => (
          <div
            key={msg.id || idx}
            style={{
              marginBottom: 12,
              display: 'flex',
              flexDirection: msg.role === 'user' ? 'row-reverse' : 'row',
            }}
          >
            <div
              style={{
                maxWidth: '80%',
                padding: '8px 14px',
                borderRadius: 12,
                background: msg.role === 'user' ? '#1890ff' : '#fff',
                color: msg.role === 'user' ? '#fff' : '#333',
                border: msg.role === 'assistant' ? '1px solid #e8e8e8' : 'none',
                whiteSpace: 'pre-wrap',
                lineHeight: 1.6,
                fontSize: 14,
              }}
            >
              {msg.content || (isStreaming ? <Spin size="small" /> : '')}
              {msg.role === 'assistant' && sources[idx] && sources[idx].length > 0 && (
                <SourceReference sources={sources[idx]} />
              )}
            </div>
          </div>
        ))}
      </div>

      {/* Input area */}
      <div style={{ display: 'flex', gap: 8 }}>
        <Input.TextArea
          value={input}
          onChange={e => setInput(e.target.value)}
          placeholder="输入问题..."
          rows={2}
          onPressEnter={e => { if (!e.shiftKey) { e.preventDefault(); handleSend(); } }}
          disabled={isStreaming}
        />
        <Button
          type="primary"
          icon={<SendOutlined />}
          onClick={handleSend}
          loading={isStreaming}
          style={{ height: 'auto' }}
        >
          发送
        </Button>
      </div>
    </div>
  );
};

export default ChatSection;
