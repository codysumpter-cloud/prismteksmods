# Everything is Crab Modding Hub 🦀

This directory is dedicated to the modding and optimization of *Everything is Crab*.

## 📂 Structure
- `/src/patches`: C# Harmony patches and BepInEx plugin source code.
- `/docs/compatibility`: Hardware-specific fixes, DLL overrides, and performance guides (especially for Intel Macs).
- `/build/bin`: Compiled `.dll` plugins ready for installation.

## 📝 To-Do List
- [ ] **May 8th:** Initial game launch and environment setup.
- [ ] **Baseline:** Document the optimal CrossOver bottle configuration.
- [ ] **Analysis:** Decompile `Assembly-CSharp.dll` to map game logic.
- [ ] **First Patch:** Implement a simple "Stat Tweak" to verify the pipeline.

## 🛠 Required Toolset
- **CrossOver / Porting Kit** (For macOS Execution)
- **BepInEx** (The modding framework)
- **Harmony** (For runtime patching)
- **dnSpy / ILSpy** (For decompilation)
- **UnityExplorer** (For live object inspection)
