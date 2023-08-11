import type { PluginListenerHandle } from '@capacitor/core';
import { registerPlugin } from '@capacitor/core';
import ResizeObserver from 'resize-observer-polyfill';

import type {
  Dimensions,
  EventListenerMap,
  EventName,
  FloatingWebPlugin as IFloatingWebPlugin,
  OpenOptions,
} from './definitions';

const FloatingWebCore = registerPlugin<IFloatingWebPlugin>('FloatingWeb', {
  web: () => import('./web').then(m => new m.FloatingWebWeb()),
});

class FloatingWebPlugin implements IFloatingWebPlugin {
  element: HTMLElement | undefined;
  resizeObserver: ResizeObserver | undefined;

  open(options: OpenOptions): Promise<void> {
    if (!options.element) {
      throw new Error('No element provided');
    }

    this.element = options.element;
    this.resizeObserver = new ResizeObserver(entries => {
      for (const _ of entries) {
        const boundingBox = options.element.getBoundingClientRect();
        this.updateDimensions({
          scale: options.scale,
          width: Math.round(boundingBox.width),
          height: Math.round(boundingBox.height),
          x: Math.round(boundingBox.x),
          y: Math.round(boundingBox.y),
        });
      }
    });
    this.resizeObserver.observe(this.element);

    const boundingBox = this.element.getBoundingClientRect();
    return FloatingWebCore.open({
      ...options,
      width: Math.round(boundingBox.width),
      height: Math.round(boundingBox.height),
      x: Math.round(boundingBox.x),
      y: Math.round(boundingBox.y),
    });
  }

  close(): Promise<void> {
    this.element = undefined;
    this.resizeObserver?.disconnect();
    FloatingWebCore.removeAllListeners();
    return FloatingWebCore.close();
  }

  loadUrl(options: { url: string }): Promise<void> {
    return FloatingWebCore.loadUrl(options);
  }

  goBack(): Promise<void> {
    return FloatingWebCore.goBack();
  }

  goForward(): Promise<void> {
    return FloatingWebCore.goForward();
  }

  reload(): Promise<void> {
    return FloatingWebCore.reload();
  }

  show(): Promise<void> {
    return FloatingWebCore.show();
  }

  hide(): Promise<void> {
    return FloatingWebCore.hide();
  }

  updateDimensions(options: Dimensions): Promise<void> {
    return FloatingWebCore.updateDimensions(options);
  }

  addListener<Event extends EventName>(
    eventName: Event,
    listener: EventListenerMap[Event],
  ): PluginListenerHandle {
    return FloatingWebCore.addListener(eventName, listener);
  }

  removeAllListeners(): Promise<void> {
    return FloatingWebCore.removeAllListeners();
  }
}

export * from './definitions';
export const FloatingWeb = new FloatingWebPlugin();
