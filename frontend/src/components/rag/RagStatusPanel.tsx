import React from 'react';
import { Descriptions, Tag, Progress } from 'antd';
import type { RagStatus } from '../../types';

interface Props {
  status: RagStatus;
}

const statusMap: Record<string, { label: string; color: string }> = {
  not_built: { label: '未构建', color: 'default' },
  built: { label: '已构建', color: 'green' },
  rebuilding: { label: '重建中', color: 'orange' },
};

const RagStatusPanel: React.FC<Props> = ({ status }) => {
  const s = statusMap[status.status] || { label: '未知', color: 'default' };

  return (
    <Descriptions size="small" column={2} bordered>
      <Descriptions.Item label="索引状态">
        <Tag color={s.color}>{s.label}</Tag>
      </Descriptions.Item>
      <Descriptions.Item label="分块大小">{status.chunkSize} 字</Descriptions.Item>
      <Descriptions.Item label="重叠字数">{status.overlap} 字</Descriptions.Item>
      <Descriptions.Item label="召回TopK">{status.topK}</Descriptions.Item>
      <Descriptions.Item label="文本总块数">{status.chunkCount}</Descriptions.Item>
      <Descriptions.Item label="总字数">{status.wordCount.toLocaleString()}</Descriptions.Item>
      <Descriptions.Item label="构建时间" span={2}>
        {status.builtAt ? new Date(status.builtAt).toLocaleString() : '-'}
      </Descriptions.Item>
    </Descriptions>
  );
};

export default RagStatusPanel;
