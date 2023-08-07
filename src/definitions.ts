import { PluginListenerHandle } from '@capacitor/core';

export interface FloatingWebPlugin {
  /**
   * Open a webview with the given URL.
   */
  open(options: OpenOptions): Promise<void>;

  /**
   * Close an open webview.
   */
  close(): Promise<void>;

  /**
   * Load a url in the webview.
   */
  loadUrl(options: { url: string }): Promise<void>;

  goBack(): Promise<void>;

  goForward(): Promise<void>;

  reload(): Promise<void>;

  updateDimensions(options: Dimensions): Promise<void>;

  addListener(
    eventName: EventName,
    listener: PageloadListener | ProgressListener | NavigateListener,
  ): PluginListenerHandle;

  removeAllListeners(): Promise<void>;
}

type PageloadListener = () => void;

type ProgressListener = (event: { value: number }) => void;

type NavigateListener = (event: {
  url: string;
  newWindow: boolean;
  sameHost: boolean;
}) => void;

export type EventListenerMap = {
  pageLoad: PageloadListener;
  progress: ProgressListener;
  navigate: NavigateListener;
};

export type EventName = keyof EventListenerMap;

export interface OpenOptions extends Dimensions {
  url: string;
  userAgent?: string;
}

export interface Dimensions {
  width: number;
  height: number;
  x: number;
  y: number;
}
