import { PluginListenerHandle, registerPlugin } from '@capacitor/core';
import ResizeObserver from 'resize-observer-polyfill';
import {
  Dimensions,
  EventListenerMap,
  EventName,
  FloatingWebPlugin,
  OpenOptions,
} from './definitions';

const FloatingWebCore = registerPlugin<FloatingWebPlugin>('FloatingWeb', {
  // web: () => import('./web').then(m => new m.FloatingWebWeb()),
});

class FloatingWebHelper implements FloatingWebPlugin {
  element: HTMLElement | undefined;
  resizeObserver: ResizeObserver | undefined;

  open(options: OpenOptions & { element: HTMLElement }): Promise<void> {
    if (!options.element) {
      throw new Error('No element provided');
    }

    this.element = options.element;
    this.resizeObserver = new ResizeObserver(entries => {
      for (const _entry of entries) {
        const boundingBox = options.element.getBoundingClientRect();
        this.updateDimensions({
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
    this.resizeObserver && this.resizeObserver.disconnect();
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
export const FloatingWeb = FloatingWebHelper;
