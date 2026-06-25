import React from 'react';

interface Props {
  title: string;
  author: string;
  source: string;
  intro: string;
  coverUrl?: string;
}

const BookInfoSection: React.FC<Props> = ({ title, author, source, intro, coverUrl }) => {
  return (
    <div style={{ display: 'flex', gap: 16, padding: 16, background: '#fff', borderRadius: 8, border: '1px solid #f0f0f0' }}>
      {coverUrl && (
        <img src={coverUrl} alt={title} style={{ width: 80, height: 110, objectFit: 'cover', borderRadius: 4 }} />
      )}
      <div style={{ flex: 1 }}>
        <h2 style={{ margin: '0 0 6px' }}>{title}</h2>
        <div style={{ color: '#666', fontSize: 13, marginBottom: 4 }}>{author || '未知作者'}</div>
        <div style={{ marginBottom: 8 }}>
          <span style={{ background: '#e6f7ff', padding: '2px 8px', borderRadius: 4, fontSize: 12 }}>{source || '未知来源'}</span>
        </div>
        <div style={{ color: '#888', fontSize: 13, lineHeight: 1.6, maxHeight: 60, overflow: 'hidden' }}>{intro || '暂无简介'}</div>
      </div>
    </div>
  );
};

export default BookInfoSection;
