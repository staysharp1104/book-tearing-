import React, { useState } from 'react';
import { Button, Space, Popconfirm, message, Progress } from 'antd';
import type { RagStatus, RagConfig } from '../../types';
import { buildRagIndex, rebuildRagIndex, clearRagIndex, clearGlobalRagCache } from '../../api/rag';

interface Props {
  bookId: string;
  status: RagStatus;
  onStatusChange: () => void;
}

const RagOperationBar: React.FC<Props> = ({ bookId, status, onStatusChange }) => {
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);

  const handleBuild = async () => {
    setLoading(true);
    setProgress(0);
    try {
      // Simulate progress
      const interval = setInterval(() => setProgress(p => Math.min(p + 10, 90)), 500);
      await buildRagIndex(bookId);
      clearInterval(interval);
      setProgress(100);
      message.success('索引构建完成');
      onStatusChange();
    } catch (e: any) {
      message.error(e.response?.data?.error || '构建失败');
    } finally {
      setLoading(false);
      setProgress(0);
    }
  };

  const handleRebuild = async () => {
    setLoading(true);
    setProgress(0);
    try {
      const interval = setInterval(() => setProgress(p => Math.min(p + 10, 90)), 500);
      await rebuildRagIndex(bookId);
      clearInterval(interval);
      setProgress(100);
      message.success('索引重建完成');
      onStatusChange();
    } catch (e: any) {
      message.error(e.response?.data?.error || '重建失败');
    } finally {
      setLoading(false);
      setProgress(0);
    }
  };

  const handleClear = async () => {
    try {
      await clearRagIndex(bookId);
      message.success('索引已清空');
      onStatusChange();
    } catch (e: any) {
      message.error('清空失败');
    }
  };

  const isBuilt = status.status === 'built';

  return (
    <div>
      <Space>
        {!isBuilt && (
          <Button type="primary" onClick={handleBuild} loading={loading}>
            构建索引
          </Button>
        )}
        {isBuilt && (
          <Button onClick={handleRebuild} loading={loading}>
            重建索引
          </Button>
        )}
        {isBuilt && (
          <Popconfirm title="确定清空索引缓存？" onConfirm={handleClear}>
            <Button danger>清空索引</Button>
          </Popconfirm>
        )}
      </Space>
      {loading && <Progress percent={progress} size="small" style={{ marginTop: 8 }} />}
    </div>
  );
};

export default RagOperationBar;
