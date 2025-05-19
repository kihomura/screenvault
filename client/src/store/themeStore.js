import { defineStore } from 'pinia';
import { themeConfig } from '../assets/theme.js';
import { storageManager } from '../utils/storageManager';

export const useThemeStore = defineStore('theme', {
    state: () => ({
        currentTheme: 'light', // Initialize with a fixed default theme
    }),
    getters: {
        themeVariables(state) {
            return themeConfig[state.currentTheme];
        },
        availableThemes() {
            return Object.keys(themeConfig);
        }
    },
    actions: {
        async refreshTheme() {
            const savedTheme = storageManager.get('app-theme', 'light');
            let themeToApply = 'light';

            if (savedTheme && themeConfig[savedTheme]) {
                themeToApply = savedTheme;
            }

            if (this.currentTheme !== themeToApply) {
                this.currentTheme = themeToApply;
            }
            this.applyTheme();
            return true;
        },
        setTheme(themeName) {
            if (themeConfig[themeName]) {
                this.currentTheme = themeName;
                storageManager.set('app-theme', themeName);
                this.applyTheme();
            } else {
                console.warn('[themeStore.js] setTheme - themeName not found in themeConfig:', themeName);
            }
        },
        applyTheme() {
            const theme = themeConfig[this.currentTheme];
            if (!theme) {
                console.error(`[themeStore.js] applyTheme - Theme "${this.currentTheme}" not found in themeConfig.`);
                return;
            }

            const extractRGB = (color) => {
                const hex = color.replace('#', '');
                const r = parseInt(hex.substring(0, 2), 16);
                const g = parseInt(hex.substring(2, 4), 16);
                const b = parseInt(hex.substring(4, 6), 16);
                return `${r}, ${g}, ${b}`;
            };

            Object.keys(theme.colors).forEach((key) => {
                if (typeof theme.colors[key] === 'object') {
                    Object.keys(theme.colors[key]).forEach((subKey) => {
                        const varName = `--${key}-${subKey}`;
                        const varValue = theme.colors[key][subKey];
                        document.documentElement.style.setProperty(varName, varValue);
                        if (varValue.startsWith('#')) {
                            document.documentElement.style.setProperty(`${varName}-rgb`, extractRGB(varValue));
                        }
                    });
                } else {
                    const varName = `--${key}`;
                    const varValue = theme.colors[key];
                    document.documentElement.style.setProperty(varName, theme.colors[key]);
                    if (varValue.startsWith('#')) {
                        document.documentElement.style.setProperty(`${varName}-rgb`, extractRGB(varValue));
                    }
                }
            });

            Object.keys(theme.typography).forEach((key) => {
                Object.keys(theme.typography[key]).forEach((subKey) => {
                    document.documentElement.style.setProperty(
                        `--font-${key}-${subKey}`,
                        theme.typography[key][subKey]
                    );
                });
            });

            Object.keys(theme.shadows).forEach((key) => {
                Object.keys(theme.shadows[key]).forEach((state) => {
                    document.documentElement.style.setProperty(
                        `--shadow-${key}-${state}`,
                        theme.shadows[key][state]
                    );
                });
            });

            Object.keys(theme.borderRadius).forEach((key) => {
                document.documentElement.style.setProperty(
                    `--border-radius-${key}`,
                    theme.borderRadius[key]
                );
            });

            Object.keys(theme.spacing).forEach((key) => {
                document.documentElement.style.setProperty(`--spacing-${key}`, theme.spacing[key]);
            });

            let currentClasses = document.body.className.split(' ');
            currentClasses = currentClasses.filter(cls => 
                !cls.startsWith('theme-') && cls !== 'no-theme-page'
            );
            document.body.className = currentClasses.join(' ').trim();
            document.body.classList.add(`theme-${this.currentTheme}`);
        },
        initTheme() {
            this.refreshTheme();
        }
    }
});