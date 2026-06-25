import React from 'react';
import { Button, Space } from 'antd';
import type { PromptTemplate } from '../../types';

interface Props {
  prompts: PromptTemplate[];
  onSelect: (prompt: PromptTemplate) => void;
  onManage: () => void;
}

const PromptToolbar: React.FC<Props> = ({ prompts, onSelect, onManage }) => {
  if (prompts.length === 0) return null;

  return (
    <div style={{ marginBottom: 12 }}>
      <Space wrap>
        {prompts.map(p => (
          <Button key={p.id} size="small" type="dashed" onClick={() => onSelect(p)}>
            {p.name}
          </Button>
        ))}
        <Button size="small" type="link" onClick={onManage}>
          管理Prompt
        </Button>
      </Space>
    </div>
  );
};

export default PromptToolbar;
