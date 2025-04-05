import { defineStore } from 'pinia';
import { themeConfig } from '../assets/theme.js';

export const useThemeStore = defineStore('theme', {
    state: () => ({
        currentTheme: localStorage.getItem('app-theme') || 'light',
    }),
    getters: {
        themeVariables() {
            return themeConfig[this.currentTheme];
        },
        availableThemes() {
            return Object.keys(themeConfig);
        }
    },
    actions: {
        setTheme(themeName) {
            if (themeConfig[themeName]) {
                this.currentTheme = themeName;
                localStorage.setItem('app-theme', themeName);
                this.applyTheme();
            }
        },
        applyTheme() {
            const theme = themeConfig[this.currentTheme];

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
                        document.documentElement.style.setProperty(
                            `--${key}-${subKey}`,
                            theme.colors[key][subKey]
                        );

                        if (theme.colors[key][subKey].startsWith('#')) {
                            document.documentElement.style.setProperty(
                                `--${key}-${subKey}-rgb`,
                                extractRGB(theme.colors[key][subKey])
                            );
                        }
                    });
                } else {
                    document.documentElement.style.setProperty(`--${key}`, theme.colors[key]);

                    if (theme.colors[key].startsWith('#')) {
                        document.documentElement.style.setProperty(
                            `--${key}-rgb`,
                            extractRGB(theme.colors[key])
                        );
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

            document.body.className = document.body.className
                .replace(/theme-\w+/g, '')
                .trim();
            document.body.classList.add(`theme-${this.currentTheme}`);
        },

        initTheme() {
            this.applyTheme();
        }
    },
});