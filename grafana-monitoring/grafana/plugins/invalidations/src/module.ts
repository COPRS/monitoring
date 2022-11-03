import { PanelPlugin } from '@grafana/data';
import { TableCellDisplayMode } from '@grafana/ui';
import { DatasourceSelectEditor } from 'components/DatasourceSelectEditor';
import { SELECTOPTIONS } from 'components/InvalidationEditor/options';
import RootCauseEditor from 'components/RootCauseEditor/Editor';
import { tablePanelChangedHandler } from 'migrations';
import { Panel } from 'SimplePanel';
import { PanelOptions } from './types';

export const plugin = new PanelPlugin<PanelOptions>(Panel)
  .setPanelChangeHandler(tablePanelChangedHandler)
  .setNoPadding()
  .useFieldConfig({
    useCustomConfig: builder => {
      builder
        .addNumberInput({
          path: 'width',
          name: 'Column width',
          settings: {
            placeholder: 'auto',
            min: 20,
            max: 300,
          },
          shouldApply: () => true,
        })
        .addRadio({
          path: 'align',
          name: 'Column alignment',
          settings: {
            options: [
              { label: 'auto', value: null },
              { label: 'left', value: 'left' },
              { label: 'center', value: 'center' },
              { label: 'right', value: 'right' },
            ],
          },
          defaultValue: null,
        })
        .addSelect({
          path: 'displayMode',
          name: 'Cell display mode',
          description: 'Color text, background, show as gauge, etc',
          settings: {
            options: [
              { value: TableCellDisplayMode.Auto, label: 'Auto' },
              { value: TableCellDisplayMode.ColorText, label: 'Color text' },
              { value: TableCellDisplayMode.ColorBackground, label: 'Color background' },
              { value: TableCellDisplayMode.GradientGauge, label: 'Gradient gauge' },
              { value: TableCellDisplayMode.LcdGauge, label: 'LCD gauge' },
              { value: TableCellDisplayMode.BasicGauge, label: 'Basic gauge' },
              { value: TableCellDisplayMode.JSONView, label: 'JSON View' },
            ],
          },
        });
    },
  })
  .setPanelOptions(builder => {
    return builder
      .addCustomEditor({
        id: 'postgresDatasourceNameField',
        path: 'postgresDatasourceName',
        name: 'Front PostgreSQL Datasource',
        description: 'Datasource used to write data into PostgreSQL database',
        editor: DatasourceSelectEditor,
        category: ['Actions'],
        settings: {
          datasourceType: 'postgres',
        },
      })
      .addCustomEditor({
        id: 'rootCauseListSensing',
        path: 'rootCauseListSensing',
        name: '',
        description: '',
        category: ['Rootcauses Lists'],
        defaultValue: SELECTOPTIONS['SENSING']['rootCause']['options'],
        editor: RootCauseEditor,
        settings: {
          category: 'Sensing',
        },
      })
      .addCustomEditor({
        id: 'rootCauseListDownlink',
        path: 'rootCauseListDownlink',
        name: '',
        description: '',
        category: ['Rootcauses Lists'],
        defaultValue: SELECTOPTIONS['DOWNLINK']['rootCause']['options'],
        editor: RootCauseEditor,
        settings: {
          category: 'Downlink',
        },
      });
  });
