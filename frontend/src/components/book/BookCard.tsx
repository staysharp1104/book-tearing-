import React from 'react';
import { Card, Button } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { Book } from '../../types';

interface Props {
  book: Book;
  onDelete?: (e: React.MouseEvent) => void;
}

const BookCard: React.FC<Props> = ({ book, onDelete }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/books/${book.bookId}`);
  };

  return (
    <Card
      hoverable
      style={{ width: 200 }}
      onClick={handleClick}
      cover={
        <div style={{ height: 260, background: '#f0f0f0', display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden', position: 'relative' }}>
          {onDelete && (
            <Button
              type="text"
              size="small"
              danger
              icon={<DeleteOutlined />}
              onClick={onDelete}
              style={{ position: 'absolute', top: 4, right: 4, zIndex: 10, background: 'rgba(255,255,255,0.8)' }}
            />
          )}
          {book.coverUrl ? (
            <img alt={book.title} src={book.coverUrl} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
          ) : (
            <span style={{ fontSize: 48, color: '#ccc' }}>{book.title.charAt(0)}</span>
          )}
        </div>
      }
    >
      <Card.Meta
        title={<div style={{ fontSize: 14, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{book.title}</div>}
        description={
          <div style={{ fontSize: 12, color: '#888' }}>
            <div>{book.author || '未知作者'}</div>
            <div style={{ marginTop: 4 }}>
              <span style={{ background: '#e6f7ff', padding: '0 6px', borderRadius: 4, fontSize: 11 }}>{book.source || '未知来源'}</span>
            </div>
          </div>
        }
      />
    </Card>
  );
};

export default BookCard;
