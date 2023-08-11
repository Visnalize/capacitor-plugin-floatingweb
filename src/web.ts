import { WebPlugin } from '@capacitor/core';

import type { Dimensions, FloatingWebPlugin, OpenOptions } from './definitions';

export class FloatingWebWeb extends WebPlugin implements FloatingWebPlugin {
  open(options: OpenOptions): Promise<void> {
    // TODO
    return Promise.resolve();
  }
  close(): Promise<void> {
    // TODO
    return Promise.resolve();
  }
  loadUrl(options: { url: string }): Promise<void> {
    // TODO
    return Promise.resolve();
  }
  goBack(): Promise<void> {
    // TODO
    return Promise.resolve();
  }
  goForward(): Promise<void> {
    // TODO
    return Promise.resolve();
  }
  reload(): Promise<void> {
    // TODO
    return Promise.resolve();
  }
  show(): Promise<void> {
    // TODO
    return Promise.resolve();
  }
  hide(): Promise<void> {
    // TODO
    return Promise.resolve();
  }
  updateDimensions(options: Dimensions): Promise<void> {
    // TODO
    return Promise.resolve();
  }
}
