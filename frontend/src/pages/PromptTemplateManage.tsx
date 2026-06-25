import React, { useEffect, useState } from 'react';
import { Table, Button, Space, Popconfirm, Tag, message, Modal, Form, Input, Switch, Upload } from 'antd';
import { PlusOutlined, ImportOutlined, ExportOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { PromptTemplate } from '../types';
import { fetchPrompts, createPrompt, updatePrompt, deletePrompt, togglePrompt, exportAllPrompts, importPrompts } from '../api/prompts';
import TemplateForm from '../components/prompt/TemplateForm';

const PromptTemplateManage: React.FC = () => {
  const navigate = useNavigate();
  const [templates, setTemplates] = useState<PromptTemplate[]>([]);
  const [editing, setEditing] = useState<PromptTemplate | null>(null);
  const [showForm, setShowForm] = useState(false);

  const load = () => fetchPrompts().then(setTemplates);
  useEffect(() => { load(); }, []);

  const handleCreate = () => { setEditing(null); setShowForm(true); };
  const handleEdit = (t: PromptTemplate) => { setEditing(t); setShowForm(true); };

  const handleSave = async (data: Partial<PromptTemplate>) => {
    try {
      if (editing) {
        await updatePrompt(editing.id, data);
        message.success('更新成功');
      } else {
        await createPrompt(data);
        message.success('创建成功');
      }
      setShowForm(false);
      load();
    } catch (e: any) {
      message.error(e.response?.data?.error || '操作失败');
    }
  };

  const handleToggle = async (id: number) => { await togglePrompt(id); load(); };
  const handleDelete = async (id: number) => {
    try {
      await deletePrompt(id);
      message.success('已删除');
      load();
    } catch (e: any) { message.error(e.response?.data?.error || '删除失败'); }
  };

  const handleExportAll = async () => {
    const data = await exportAllPrompts();
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href = url; a.download = 'prompt_templates.json'; a.click();
    URL.revokeObjectURL(url);
  };

  const handleImport = async (file: File) => {
    try {
      const text = await file.text();
      const data = JSON.parse(text);
      await importPrompts(Array.isArray(data) ? data : [data]);
      message.success('导入成功');
      load();
    } catch { message.error('导入失败'); }
    return false;
  };

  const columns = [
    { title: '名称', dataIndex: 'name', key: 'name', width: 120 },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: '场景', dataIndex: 'scene', key: 'scene', width: 80 },
    { title: '快捷按钮', dataIndex: 'isQuickBtn', key: 'isQuickBtn', width: 80, render: (v: boolean) => v ? <Tag color="blue">是</Tag> : '-' },
    { title: '系统内置', dataIndex: 'isSystem', key: 'isSystem', width: 80, render: (v: boolean) => v ? <Tag color="orange">系统</Tag> : '-' },
    { title: '启用', dataIndex: 'enabled', key: 'enabled', width: 60, render: (v: boolean) => v ? <Tag color="green">开</Tag> : <Tag color="red">关</Tag> },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 150, render: (v: string) => v ? new Date(v).toLocaleDateString() : '-' },
    {
      title: '操作', key: 'action', width: 220,
      render: (_: any, r: PromptTemplate) => (
        <Space size="small">
          <Button size="small" onClick={() => handleEdit(r)}>编辑</Button>
          <Button size="small" onClick={() => handleToggle(r.id)}>{r.enabled ? '禁用' : '启用'}</Button>
          {!r.isSystem && (
            <Popconfirm title="确定删除？" onConfirm={() => handleDelete(r.id)}>
              <Button size="small" danger>删除</Button>
            </Popconfirm>
          )}
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: 24 }}>
      <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/')} style={{ marginBottom: 16 }}>返回书库</Button>
      <h1 style={{ marginBottom: 16 }}>Prompt模板管理</h1>

      <Space style={{ marginBottom: 12 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增模板</Button>
        <Button icon={<ExportOutlined />} onClick={handleExportAll}>批量导出</Button>
        <Upload beforeUpload={handleImport} showUploadList={false} accept=".json">
          <Button icon={<ImportOutlined />}>批量导入</Button>
        </Upload>
      </Space>

      {showForm && (
        <div style={{ marginBottom: 12, padding: 16, background: '#fafafa', borderRadius: 8, maxWidth: 600 }}>
          <TemplateForm initial={editing} onSave={handleSave} onCancel={() => setShowForm(false)} />
        </div>
      )}

      <Table dataSource={templates} columns={columns} rowKey="id" size="small" pagination={{ pageSize: 10 }} />
    </div>
  );
};

export default PromptTemplateManage;
