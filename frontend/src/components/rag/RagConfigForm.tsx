import React from 'react';
import { Form, InputNumber, Switch, Button, Space } from 'antd';
import type { RagConfig } from '../../types';

interface Props {
  config: RagConfig;
  onSave: (config: RagConfig) => void;
}

const RagConfigForm: React.FC<Props> = ({ config, onSave }) => {
  const [form] = Form.useForm();

  const handleSave = () => {
    form.validateFields().then(values => onSave(values as RagConfig));
  };

  return (
    <Form
      form={form}
      layout="inline"
      initialValues={config}
      style={{ flexWrap: 'wrap', gap: 8 }}
    >
      <Form.Item label="分块大小" name="chunkSize">
        <InputNumber min={100} max={2000} />
      </Form.Item>
      <Form.Item label="重叠字数" name="overlap">
        <InputNumber min={0} max={500} />
      </Form.Item>
      <Form.Item label="召回TopK" name="topK">
        <InputNumber min={1} max={20} />
      </Form.Item>
      <Form.Item label="短书全文注入" name="shortBookFullText" valuePropName="checked">
        <Switch />
      </Form.Item>
      <Form.Item>
        <Button type="primary" onClick={handleSave}>保存配置</Button>
      </Form.Item>
    </Form>
  );
};

export default RagConfigForm;
