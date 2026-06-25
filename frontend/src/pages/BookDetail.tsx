import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, Spin, Divider, Space, message } from 'antd';
import { ArrowLeftOutlined, ThunderboltOutlined } from '@ant-design/icons';
import type { Book, RagStatus, RagConfig } from '../types';
import { fetchBook } from '../api/books';
import { fetchRagStatus, updateRagConfig } from '../api/rag';
import RagStatusPanel from '../components/rag/RagStatusPanel';
import RagConfigForm from '../components/rag/RagConfigForm';
import RagOperationBar from '../components/rag/RagOperationBar';

const BookDetail: React.FC = () => {
  const { bookId } = useParams<{ bookId: string }>();
  const navigate = useNavigate();
  const [book, setBook] = useState<Book | null>(null);
  const [ragStatus, setRagStatus] = useState<RagStatus | null>(null);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    if (!bookId) return;
    setLoading(true);
    try {
      const [b, r] = await Promise.all([
        fetchBook(bookId),
        fetchRagStatus(bookId),
      ]);
      setBook(b);
      setRagStatus(r);
    } catch { message.error('加载失败'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [bookId]);

  const handleSaveConfig = async (config: RagConfig) => {
    if (!bookId) return;
    try {
      await updateRagConfig(bookId, config);
      message.success('配置已更新');
      load();
    } catch { message.error('更新配置失败'); }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: 60 }}><Spin size="large" /></div>;
  if (!book) return <div style={{ textAlign: 'center', padding: 60, color: '#aaa' }}>书籍不存在</div>;

  return (
    <div style={{ padding: 24, maxWidth: 900, margin: '0 auto' }}>
      <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/')} style={{ marginBottom: 16 }}>返回书库</Button>

      <Card title={book.title} style={{ marginBottom: 16 }}>
        <p>作者: {book.author}</p>
        <p>来源: {book.source}</p>
        <p>简介: {book.intro}</p>
      </Card>

      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<ThunderboltOutlined />} onClick={() => navigate(`/books/${bookId}/analysis`)}>
          进入AI拆书
        </Button>
      </Space>

      <Divider>RAG 索引管理</Divider>

      {ragStatus && (
        <>
          <RagStatusPanel status={ragStatus} />
          <Divider />
          <RagOperationBar bookId={bookId!} status={ragStatus} onStatusChange={load} />
          <Divider />
          <RagConfigForm config={{ chunkSize: ragStatus.chunkSize, overlap: ragStatus.overlap, topK: ragStatus.topK, shortBookFullText: ragStatus.shortBookFullText }} onSave={handleSaveConfig} />
        </>
      )}
    </div>
  );
};

export default BookDetail;
