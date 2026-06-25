import React, { useEffect, useState, useCallback } from 'react';
import { Row, Col, Spin, Button, Select, Input, Space, Empty, Pagination, message } from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { Book } from '../types';
import BookCard from '../components/book/BookCard';
import { searchBooks, fetchCategories, fetchSources, deleteBook } from '../api/books';
import type { BookSearchResult } from '../api/books';

const { Option } = Select;

const PAGE_SIZE = 50;

const BookLibrary: React.FC = () => {
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);

  // Filter state
  const [categories, setCategories] = useState<string[]>([]);
  const [sources, setSources] = useState<string[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedSource, setSelectedSource] = useState<string>('all');
  const [keyword, setKeyword] = useState('');

  const navigate = useNavigate();

  useEffect(() => {
    fetchCategories().then(setCategories).catch(() => {});
    fetchSources().then(setSources).catch(() => {});
  }, []);

  const loadBooks = useCallback(async (pageNum: number) => {
    setLoading(true);
    try {
      const params: any = {};
      if (selectedCategory !== 'all') params.category = selectedCategory;
      if (selectedSource !== 'all') params.source = selectedSource;
      if (keyword.trim()) params.keyword = keyword.trim();

      if (params.category || params.source || params.keyword) {
        params.page = pageNum;
        params.size = PAGE_SIZE;
        const result = await searchBooks(params) as BookSearchResult;
        setBooks(result.content);
        setTotal(result.totalElements);
        setPage(result.number);
      } else {
        const allBooks = await searchBooks({}) as Book[];
        setBooks(allBooks);
        setTotal(allBooks.length);
        setPage(0);
      }
    } catch {
      message.error('加载书籍失败');
    } finally {
      setLoading(false);
    }
  }, [selectedCategory, selectedSource, keyword]);

  useEffect(() => {
    loadBooks(0);
  }, []);

  const handleSearch = () => {
    loadBooks(0);
  };

  const handleReset = () => {
    setSelectedCategory('all');
    setSelectedSource('all');
    setKeyword('');
    // Re-load with no filters
    setLoading(true);
    searchBooks({}).then((r) => {
      const all = r as Book[];
      setBooks(all);
      setTotal(all.length);
      setPage(0);
    }).finally(() => setLoading(false));
  };

  const handlePageChange = (newPage: number) => {
    loadBooks(newPage - 1);
  };

  const handleDeleteBook = async (bookId: string, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await deleteBook(bookId);
      message.success('书籍已删除');
      loadBooks(page);
    } catch {
      message.error('删除失败');
    }
  };

  return (
    <div style={{ padding: 24 }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h1 style={{ margin: 0 }}>书库</h1>
        <Space>
          <Button type="link" onClick={() => navigate('/admin/prompts')}>Prompt管理</Button>
        </Space>
      </div>

      {/* Search/Filter bar */}
      <div style={{
        marginBottom: 20,
        padding: '12px 16px',
        background: '#fafafa',
        borderRadius: 8,
        border: '1px solid #f0f0f0',
        display: 'flex',
        gap: 12,
        alignItems: 'center',
        flexWrap: 'wrap',
      }}>
        <Select
          value={selectedCategory}
          onChange={setSelectedCategory}
          style={{ width: 140 }}
          placeholder="全部分类"
        >
          <Option value="all">全部分类</Option>
          {categories.map(c => <Option key={c} value={c}>{c}</Option>)}
        </Select>

        <Select
          value={selectedSource}
          onChange={setSelectedSource}
          style={{ width: 120 }}
          placeholder="全部平台"
        >
          <Option value="all">全部平台</Option>
          {sources.map(s => <Option key={s} value={s}>{s}</Option>)}
        </Select>

        <Input
          value={keyword}
          onChange={e => setKeyword(e.target.value)}
          onPressEnter={handleSearch}
          placeholder="书名/作者模糊搜索"
          style={{ width: 220 }}
          prefix={<SearchOutlined />}
          allowClear
        />

        <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>查询</Button>
        <Button icon={<ReloadOutlined />} onClick={handleReset}>重置</Button>
      </div>

      {/* Book grid */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: 60 }}><Spin size="large" /></div>
      ) : books.length === 0 ? (
        <div style={{ textAlign: 'center', padding: 60, color: '#aaa' }}>
          <Empty description="暂无匹配书籍" />
        </div>
      ) : (
        <>
          <Row gutter={[16, 16]}>
            {books.map(book => (
              <Col key={book.bookId}>
                <BookCard
                  book={book}
                  onDelete={(e) => handleDeleteBook(book.bookId, e)}
                />
              </Col>
            ))}
          </Row>

          {total > PAGE_SIZE && (
            <div style={{ textAlign: 'center', marginTop: 24 }}>
              <Pagination
                current={page + 1}
                total={total}
                pageSize={PAGE_SIZE}
                onChange={handlePageChange}
                showTotal={t => `共 ${t} 本`}
                showSizeChanger={false}
              />
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default BookLibrary;
