import React from 'react';

interface ChapterItem {
  index: number;
  title: string;
  content: string;
}

interface Props {
  chapters: ChapterItem[];
  onScrollToChapter?: (index: number) => void;
}

const ChapterContentSection: React.FC<Props> = ({ chapters, onScrollToChapter }) => {
  const refs = React.useRef<Record<number, HTMLDivElement | null>>({});

  React.useEffect(() => {
    if (onScrollToChapter && chapters.length > 0) {
      // If jump requested, scroll to chapter
    }
  }, [onScrollToChapter, chapters]);

  if (chapters.length === 0) {
    return (
      <div style={{ padding: 40, textAlign: 'center', color: '#aaa' }}>
        暂无章节内容
      </div>
    );
  }

  return (
    <div style={{ maxHeight: 500, overflow: 'auto' }}>
      {chapters.map(ch => (
        <div
          key={ch.index}
          ref={el => { refs.current[ch.index] = el; }}
          style={{ marginBottom: 16, padding: 12, background: '#fff', borderRadius: 6, border: '1px solid #f0f0f0' }}
        >
          <h4 style={{ margin: '0 0 8px', color: '#333' }}>第{ch.index}章 {ch.title}</h4>
          <div style={{ fontSize: 14, lineHeight: 1.8, color: '#555', whiteSpace: 'pre-wrap' }}>
            {ch.content || '（内容加载中...）'}
          </div>
        </div>
      ))}
    </div>
  );
};

export default ChapterContentSection;
