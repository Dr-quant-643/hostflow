// Next.js's SWC/webpack pipeline handles bare CSS imports (static or
// dynamic) at build time without needing this — but `tsc --noEmit` alone
// doesn't know what a "*.css" import resolves to, so any dynamic
// `import("some-package/dist/styles.css")` fails type-checking without an
// ambient declaration.
declare module "*.css" {
  const content: { [className: string]: string };
  export default content;
}
