# capacitor-plugin-floatingweb

Capacitor plugin to create floating Webviews.

## Install

```bash
npm install capacitor-plugin-floatingweb
npx cap sync
```

## API

<docgen-index>

* [`open(...)`](#open)
* [`close()`](#close)
* [`loadUrl(...)`](#loadurl)
* [`goBack()`](#goback)
* [`goForward()`](#goforward)
* [`reload()`](#reload)
* [`show()`](#show)
* [`hide()`](#hide)
* [`updateDimensions(...)`](#updatedimensions)
* [`addListener(keyof EventListenerMap, ...)`](#addlistenerkeyof-eventlistenermap)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### open(...)

```typescript
open(options: OpenOptions) => Promise<void>
```

Open a webview with the given URL.

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#openoptions">OpenOptions</a></code> |

--------------------


### close()

```typescript
close() => Promise<void>
```

Close an open webview.

--------------------


### loadUrl(...)

```typescript
loadUrl(options: { url: string; }) => Promise<void>
```

Load a url in the webview.

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ url: string; }</code> |

--------------------


### goBack()

```typescript
goBack() => Promise<void>
```

--------------------


### goForward()

```typescript
goForward() => Promise<void>
```

--------------------


### reload()

```typescript
reload() => Promise<void>
```

--------------------


### show()

```typescript
show() => Promise<void>
```

--------------------


### hide()

```typescript
hide() => Promise<void>
```

--------------------


### updateDimensions(...)

```typescript
updateDimensions(options: Dimensions) => Promise<void>
```

| Param         | Type                                              |
| ------------- | ------------------------------------------------- |
| **`options`** | <code><a href="#dimensions">Dimensions</a></code> |

--------------------


### addListener(keyof EventListenerMap, ...)

```typescript
addListener(eventName: EventName, listener: PageloadListener | ProgressListener | NavigateListener) => PluginListenerHandle
```

| Param           | Type                                                                                                                                                                  |
| --------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`eventName`** | <code>keyof <a href="#eventlistenermap">EventListenerMap</a></code>                                                                                                   |
| **`listener`**  | <code><a href="#pageloadlistener">PageloadListener</a> \| <a href="#progresslistener">ProgressListener</a> \| <a href="#navigatelistener">NavigateListener</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

--------------------


### Interfaces


#### OpenOptions

| Prop               | Type                     |
| ------------------ | ------------------------ |
| **`url`**          | <code>string</code>      |
| **`element`**      | <code>HTMLElement</code> |
| **`userAgent`**    | <code>string</code>      |
| **`wideViewport`** | <code>boolean</code>     |


#### Dimensions

| Prop         | Type                |
| ------------ | ------------------- |
| **`width`**  | <code>number</code> |
| **`height`** | <code>number</code> |
| **`x`**      | <code>number</code> |
| **`y`**      | <code>number</code> |
| **`scale`**  | <code>number</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


### Type Aliases


#### EventName

<code>keyof <a href="#eventlistenermap">EventListenerMap</a></code>


#### EventListenerMap

<code>{ pageLoad: <a href="#pageloadlistener">PageloadListener</a>; progress: <a href="#progresslistener">ProgressListener</a>; navigate: <a href="#navigatelistener">NavigateListener</a>; }</code>


#### PageloadListener

<code>(): void</code>


#### ProgressListener

<code>(event: { value: number; }): void</code>


#### NavigateListener

<code>(event: { url: string; newWindow: boolean; sameHost: boolean; }): void</code>

</docgen-api>
