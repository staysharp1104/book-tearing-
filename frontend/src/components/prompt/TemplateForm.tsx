import React from 'react';
import { Form, Input, Switch, Button, Space } from 'antd';
import type { PromptTemplate } from '../../types';

interface Props {
  initial: PromptTemplate | null;
  onSave: (data: Partial<PromptTemplate>) => void;
  onCancel: () => void;
}

const TemplateForm: React.FC<Props> = ({ initial, onSave, onCancel }) => {
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (initial) form.setFieldsValue(initial);
    else form.resetFields();
  }, [initial, form]);

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={initial || { name: '', description: '', scene: 'analysis', content: '', isQuickBtn: true, sortOrder: 0 }}
      onFinish={onSave}
      style={{ maxWidth: 600 }}
    >
      <Form.Item label="模板名称" name="name" rules={[{ required: true, message: '请输入名称' }]}>
        <Input />
      </Form.Item>
      <Form.Item label="描述" name="description">
        <Input />
      </Form.Item>
      <Form.Item label="Prompt内容" name="content" rules={[{ required: true, message: '请输入Prompt内容' }]}>
        <Input.TextArea rows={6} />
      </Form.Item>
      <Form.Item label="快捷按钮" name="isQuickBtn" valuePropName="checked">
        <Switch />
      </Form.Item>
      <Form.Item>
        <Space>
          <Button type="primary" htmlType="submit">保存</Button>
          <Button onClick={onCancel}>取消</Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default TemplateForm;
