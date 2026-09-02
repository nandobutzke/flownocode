import { useEffect, useSyncExternalStore } from "react";
import { useEditorStore } from "@/store/editor-store";

export function useEditorHydrated() {
  useEffect(() => {
    void useEditorStore.persist.rehydrate();
  }, []);

  return useSyncExternalStore(
    (onChange) => useEditorStore.persist.onFinishHydration(onChange),
    () => useEditorStore.persist.hasHydrated(),
    () => false,
  );
}
