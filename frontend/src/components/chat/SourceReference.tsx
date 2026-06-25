import React from 'react';
import type { SourceRecord } from '../../types';

interface Props {
  sources: SourceRecord[];
  onJumpToChapter?: (chapterIndex: number) => void;
}

const SourceReference: React.FC<Props> = ({ sources, onJumpToChapter }) => {
  if (!sources || sources.length === 0) return null;

  return (
    <div style={{ marginTop: 8, padding: '8px 12px', background: '#fafafa', borderRadius: 6, border: '1px solid #f0f0f0' }}>
      <div style={{ fontSize: 12, color: '#888', marginBottom: 6 }}>参考来源 (Top {sources.length})</div>
      {sources.map(s => (
        <div key={s.rank} style={{ fontSize: 12, marginBottom: 6, padding: 6, background: '#fff', borderRadius: 4 }}>
          <div style={{ color: '#1890ff', marginBottom: 2 }}>
            #{s.rank} 第{s.chapterIndex}章 {s.chapterTitle}
            {onJumpToChapter && (
              <a style={{ marginLeft: 8, cursor: 'pointer' }} onClick={() => onJumpToChapter(s.chapterIndex)}>查看原文</a>
            )}
          </div>
          <div style={{ color: '#666', lineHeight: 1.5 }}>{s.excerpt}</div>
        </div>
      ))}
    </div>
  );
};

export default SourceReference;
