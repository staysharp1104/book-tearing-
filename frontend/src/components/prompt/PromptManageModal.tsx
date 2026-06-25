import React, { useEffect, useState } from 'react';
import { Modal, Table, Button, Space, Popconfirm, Tag, message, Upload } from 'antd';
import { PlusOutlined, ImportOutlined, ExportOutlined } from '@ant-design/icons';
import type { PromptTemplate } from '../../types';
import { fetchPrompts, createPrompt, updatePrompt, deletePrompt, togglePrompt, exportPrompt, exportAllPrompts, importPrompts } from '../../api/prompts';
import TemplateForm from './TemplateForm';

interface Props {
  open: boolean;
  onClose: () => void;
  onSaved?: () => void;
}

const PromptManageModal: React.FC<Props> = ({ open, onClose, onSaved }) => {
  const [templates, setTemplates] = useState<PromptTemplate[]>([]);
  const [editing, setEditing] = useState<PromptTemplate | null>(null);
  const [showForm, setShowForm] = useState(false);

  const load = () => fetchPrompts().then(setTemplates);

  useEffect(() => { if (open) load(); }, [open]);

  const handleCreate = () => {
    setEditing(null);
    setShowForm(true);
  };

  const handleEdit = (t: PromptTemplate) => {
    setEditing(t);
    setShowForm(true);
  };

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
      onSaved?.();
    } catch (e: any) {
      message.error(e.response?.data?.error || '操作失败');
    }
  };

  const handleToggle = async (id: number) => {
    await togglePrompt(id);
    load();
    onSaved?.();
  };

  const handleDelete = async (id: number) => {
    try {
      await deletePrompt(id);
      message.success('已删除');
      load();
      onSaved?.();
    } catch (e: any) {
      message.error(e.response?.data?.error || '删除失败');
    }
  };

  const handleExportAll = async () => {
    const data = await exportAllPrompts();
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'prompt_templates.json';
    a.click();
    URL.revokeObjectURL(url);
  };

  const handleImport = async (file: File) => {
    try {
      const text = await file.text();
      const data = JSON.parse(text);
      const list = Array.isArray(data) ? data : [data];
      await importPrompts(list);
      message.success(`导入 ${list.length} 个模板`);
      load();
      onSaved?.();
    } catch {
      message.error('导入失败，请检查文件格式');
    }
    return false;
  };

  const columns = [
    { title: '名称', dataIndex: 'name', key: 'name', width: 120 },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: '场景', dataIndex: 'scene', key: 'scene', width: 80 },
    {
      title: '快捷按钮', dataIndex: 'isQuickBtn', key: 'isQuickBtn', width: 80,
      render: (v: boolean) => v ? <Tag color="blue">是</Tag> : '-'
    },
    {
      title: '启用', dataIndex: 'enabled', key: 'enabled', width: 60,
      render: (v: boolean) => v ? <Tag color="green">开</Tag> : <Tag color="red">关</Tag>
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 150, render: (v: string) => v ? new Date(v).toLocaleDateString() : '-' },
    {
      title: '操作', key: 'action', width: 200,
      render: (_: any, record: PromptTemplate) => (
        <Space size="small">
          <Button size="small" onClick={() => handleEdit(record)}>编辑</Button>
          <Button size="small" onClick={() => handleToggle(record.id)}>
            {record.enabled ? '禁用' : '启用'}
          </Button>
          {!record.isSystem && (
            <Popconfirm title="确定删除？" onConfirm={() => handleDelete(record.id)}>
              <Button size="small" danger>删除</Button>
            </Popconfirm>
          )}
        </Space>
      )
    }
  ];

  return (
    <Modal title="Prompt模板管理" open={open} onCancel={onClose} width={900} footer={null}>
      <Space style={{ marginBottom: 12 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增模板</Button>
        <Button icon={<ExportOutlined />} onClick={handleExportAll}>批量导出</Button>
        <Upload beforeUpload={handleImport} showUploadList={false} accept=".json">
          <Button icon={<ImportOutlined />}>批量导入</Button>
        </Upload>
      </Space>

      {showForm && (
        <div style={{ marginBottom: 12, padding: 12, background: '#fafafa', borderRadius: 8 }}>
          <TemplateForm initial={editing} onSave={handleSave} onCancel={() => setShowForm(false)} />
        </div>
      )}

      <Table
        dataSource={templates}
        columns={columns}
        rowKey="id"
        size="small"
        pagination={{ pageSize: 8 }}
      />
    </Modal>
  );
};

export default PromptManageModal;
