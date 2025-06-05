/**
 * Theme Store using Pinia
 * Manages application-wide theme state and CSS variable application
 * Supports multiple themes with user-specific persistence
 */

import { defineStore } from 'pinia';
import { themeConfig } from '../assets/theme.js';
import { storageManager } from '../utils/storageManager';

export const useThemeStore = defineStore('theme', {
    state: () => ({
        currentTheme: 'light', // Initialize with light theme as default
    }),
    
    getters: {
        /**
         * Get CSS variables for the current theme
         * @returns {Object} Theme configuration object with colors, typography, etc.
         */
        themeVariables(state) {
            return themeConfig[state.currentTheme];
        },
        
        /**
         * Get list of available theme names
         * @returns {string[]} Array of available theme names
         */
        availableThemes() {
            return Object.keys(themeConfig);
        }
    },
    
    actions: {
        /**
         * Refresh theme from storage and apply it
         * Used when user authentication state changes or app initializes
         * @returns {boolean} True when theme is successfully applied
         */
        async refreshTheme() {
            const savedTheme = storageManager.get('app-theme', 'light');
            let themeToApply = 'light';

            // Validate saved theme exists in theme configuration
            if (savedTheme && themeConfig[savedTheme]) {
                themeToApply = savedTheme;
            }

            // Update current theme if different
            if (this.currentTheme !== themeToApply) {
                this.currentTheme = themeToApply;
            }
            
            this.applyTheme();
            return true;
        },
        
        /**
         * Set and persist a new theme
         * @param {string} themeName - Name of the theme to apply
         */
        setTheme(themeName) {
            if (themeConfig[themeName]) {
                this.currentTheme = themeName;
                storageManager.set('app-theme', themeName);
                this.applyTheme();
            } else {
                console.warn('[themeStore.js] setTheme - themeName not found in themeConfig:', themeName);
            }
        },
        
        /**
         * Apply current theme to DOM by setting CSS custom properties
         * Updates document root styles and body classes
         */
        applyTheme() {
            const theme = themeConfig[this.currentTheme];
            if (!theme) {
                console.error(`[themeStore.js] applyTheme - Theme "${this.currentTheme}" not found in themeConfig.`);
                return;
            }

            /**
             * Extract RGB values from hex color for CSS rgb() functions
             * @param {string} color - Hex color string
             * @returns {string} RGB values as comma-separated string
             */
            const extractRGB = (color) => {
                const hex = color.replace('#', '');
                const r = parseInt(hex.substring(0, 2), 16);
                const g = parseInt(hex.substring(2, 4), 16);
                const b = parseInt(hex.substring(4, 6), 16);
                return `${r}, ${g}, ${b}`;
            };

            // Apply color variables to document root
            Object.keys(theme.colors).forEach((key) => {
                if (typeof theme.colors[key] === 'object') {
                    // Handle nested color objects (e.g., primary: { light: '#...', dark: '#...' })
                    Object.keys(theme.colors[key]).forEach((subKey) => {
                        const varName = `--${key}-${subKey}`;
                        const varValue = theme.colors[key][subKey];
                        document.documentElement.style.setProperty(varName, varValue);
                        
                        // Create RGB variant for transparency usage
                        if (varValue.startsWith('#')) {
                            document.documentElement.style.setProperty(`${varName}-rgb`, extractRGB(varValue));
                        }
                    });
                } else {
                    // Handle simple color values
                    const varName = `--${key}`;
                    const varValue = theme.colors[key];
                    document.documentElement.style.setProperty(varName, theme.colors[key]);
                    
                    // Create RGB variant for transparency usage
                    if (varValue.startsWith('#')) {
                        document.documentElement.style.setProperty(`${varName}-rgb`, extractRGB(varValue));
                    }
                }
            });

            // Apply typography variables
            Object.keys(theme.typography).forEach((key) => {
                Object.keys(theme.typography[key]).forEach((subKey) => {
                    document.documentElement.style.setProperty(
                        `--font-${key}-${subKey}`,
                        theme.typography[key][subKey]
                    );
                });
            });

            // Apply shadow variables
            Object.keys(theme.shadows).forEach((key) => {
                Object.keys(theme.shadows[key]).forEach((state) => {
                    document.documentElement.style.setProperty(
                        `--shadow-${key}-${state}`,
                        theme.shadows[key][state]
                    );
                });
            });

            // Apply border radius variables
            Object.keys(theme.borderRadius).forEach((key) => {
                document.documentElement.style.setProperty(
                    `--border-radius-${key}`,
                    theme.borderRadius[key]
                );
            });

            // Apply spacing variables
            Object.keys(theme.spacing).forEach((key) => {
                document.documentElement.style.setProperty(`--spacing-${key}`, theme.spacing[key]);
            });

            // Update body class for theme-specific styling
            let currentClasses = document.body.className.split(' ');
            currentClasses = currentClasses.filter(cls => 
                !cls.startsWith('theme-') && cls !== 'no-theme-page'
            );
            document.body.className = currentClasses.join(' ').trim();
            document.body.classList.add(`theme-${this.currentTheme}`);
        },
        
        /**
         * Initialize theme on store creation
         * Convenience method that calls refreshTheme
         */
        initTheme() {
            this.refreshTheme();
        }
    }
});