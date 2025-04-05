export const themeConfig = {
    light: {
        name: 'light',
        colors: {
            primary: '#3b82f6',
            secondary: '#6366f1',
            tertiary: '#8b5cf6',
            'primary-dark': '#2563eb',
            'primary-rgb': '59, 130, 246',

            background: {
                base: '#ffffff',
                subtle: '#f8fafc',
                muted: '#f1f5f9'
            },

            text: {
                primary: '#1e293b',
                secondary: '#475569',
                muted: '#64748b'
            },

            accent: {
                success: '#22c55e',
                warning: '#f59e0b',
                error: '#ef4444',
                info: '#0ea5e9'
            },

            interactive: {
                hover: '#e2e8f0',
                active: '#cbd5e1',
                focus: '#94a3b8'
            },

            border: {
                light: '#e2e8f0',
                medium: '#cbd5e1',
                dark: '#94a3b8'
            }
        },

        typography: {
            fontFamily: {
                primary: "'Inter', 'Helvetica Neue', Arial, sans-serif",
                secondary: "'Poppins', sans-serif",
                mono: "'Fira Code', monospace"
            },
            fontSize: {
                xs: '0.75rem',
                sm: '0.875rem',
                base: '1rem',
                lg: '1.125rem',
                xl: '1.25rem',
                xxl: '1.5rem'
            },
            fontWeight: {
                light: 300,
                regular: 400,
                medium: 500,
                semibold: 600,
                bold: 700
            },
            lineHeight: {
                tight: 1.2,
                normal: 1.5,
                relaxed: 1.8
            }
        },

        shadows: {
            level1: {
                default: '0 1px 3px rgba(0,0,0,0.05), 0 1px 2px rgba(0,0,0,0.1)',
                hover: '0 2px 4px rgba(0,0,0,0.08), 0 2px 3px rgba(0,0,0,0.12)'
            },
            level2: {
                default: '0 3px 6px rgba(0,0,0,0.1), 0 3px 4px rgba(0,0,0,0.06)',
                hover: '0 4px 8px rgba(0,0,0,0.12), 0 3px 6px rgba(0,0,0,0.08)'
            },
            level3: {
                default: '0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -2px rgba(0,0,0,0.05)',
                hover: '0 15px 20px -3px rgba(0,0,0,0.12), 0 6px 10px -2px rgba(0,0,0,0.08)'
            }
        },

        borderRadius: {
            sm: '0.25rem',
            md: '0.375rem',
            lg: '0.5rem',
            xl: '0.75rem',
            full: '9999px'
        },

        spacing: {
            xs: '0.25rem',
            sm: '0.5rem',
            md: '0.75rem',
            lg: '1rem',
            xl: '1.5rem',
            xxl: '2rem'
        }
    },

    dark: {
        name: 'dark',
        colors: {
            primary: '#60a5fa',
            secondary: '#818cf8',
            tertiary: '#a78bfa',
            'primary-dark': '#3b82f6',
            'primary-rgb': '96, 165, 250',

            background: {
                base: '#0f172a',
                subtle: '#1e293b',
                muted: '#334155'
            },

            text: {
                primary: '#f8fafc',
                secondary: '#e2e8f0',
                muted: '#cbd5e1'
            },

            accent: {
                success: '#4ade80',
                warning: '#fbbf24',
                error: '#f87171',
                info: '#38bdf8'
            },

            interactive: {
                hover: '#1e293b',
                active: '#334155',
                focus: '#475569'
            },

            border: {
                light: '#334155',
                medium: '#475569',
                dark: '#64748b'
            }
        },

        typography: {
            fontFamily: {
                primary: "'Inter', 'Helvetica Neue', Arial, sans-serif",
                secondary: "'Poppins', sans-serif",
                mono: "'Fira Code', monospace"
            },
            fontSize: {
                xs: '0.75rem',
                sm: '0.875rem',
                base: '1rem',
                lg: '1.125rem',
                xl: '1.25rem',
                xxl: '1.5rem'
            },
            fontWeight: {
                light: 300,
                regular: 400,
                medium: 500,
                semibold: 600,
                bold: 700
            },
            lineHeight: {
                tight: 1.2,
                normal: 1.5,
                relaxed: 1.8
            }
        },

        shadows: {
            level1: {
                default: '0 1px 3px rgba(0,0,0,0.5), 0 1px 2px rgba(0,0,0,0.6)',
                hover: '0 2px 4px rgba(0,0,0,0.6), 0 2px 3px rgba(0,0,0,0.7)'
            },
            level2: {
                default: '0 3px 6px rgba(0,0,0,0.6), 0 3px 4px rgba(0,0,0,0.7)',
                hover: '0 4px 8px rgba(0,0,0,0.7), 0 3px 6px rgba(0,0,0,0.8)'
            },
            level3: {
                default: '0 10px 15px -3px rgba(0,0,0,0.7), 0 4px 6px -2px rgba(0,0,0,0.8)',
                hover: '0 15px 20px -3px rgba(0,0,0,0.8), 0 6px 10px -2px rgba(0,0,0,0.9)'
            }
        },

        borderRadius: {
            sm: '0.25rem',
            md: '0.375rem',
            lg: '0.5rem',
            xl: '0.75rem',
            full: '9999px'
        },

        spacing: {
            xs: '0.25rem',
            sm: '0.5rem',
            md: '0.75rem',
            lg: '1rem',
            xl: '1.5rem',
            xxl: '2rem'
        }
    },

    cyberpunk: {
        name: 'cyberpunk',
        colors: {
            primary: '#ff2a6d',
            secondary: '#05d9e8',
            tertiary: '#d1f7ff',
            'primary-dark': '#ff0055',
            'primary-rgb': '255, 42, 109',

            background: {
                base: '#01012b',
                subtle: '#080838',
                muted: '#101045'
            },

            text: {
                primary: '#d1f7ff',
                secondary: '#05d9e8',
                muted: '#7678ed'
            },

            accent: {
                success: '#7fff00',
                warning: '#ffd300',
                error: '#ff2a6d',
                info: '#05d9e8'
            },

            interactive: {
                hover: '#7678ed33',
                active: '#05d9e833',
                focus: '#ff2a6d33'
            },

            border: {
                light: '#05d9e8',
                medium: '#7678ed',
                dark: '#ff2a6d'
            }
        },

        typography: {
            fontFamily: {
                primary: "'Orbitron', sans-serif",
                secondary: "'Rajdhani', sans-serif",
                mono: "'VT323', monospace"
            },
            fontSize: {
                xs: '0.75rem',
                sm: '0.875rem',
                base: '1rem',
                lg: '1.125rem',
                xl: '1.25rem',
                xxl: '1.5rem'
            },
            fontWeight: {
                light: 300,
                regular: 400,
                medium: 500,
                semibold: 600,
                bold: 700
            },
            lineHeight: {
                tight: 1.2,
                normal: 1.5,
                relaxed: 1.8
            }
        },

        shadows: {
            level1: {
                default: '0 0 5px rgba(5, 217, 232, 0.5)',
                hover: '0 0 8px rgba(5, 217, 232, 0.7)'
            },
            level2: {
                default: '0 0 10px rgba(255, 42, 109, 0.5), 0 0 5px rgba(5, 217, 232, 0.3)',
                hover: '0 0 15px rgba(255, 42, 109, 0.7), 0 0 10px rgba(5, 217, 232, 0.5)'
            },
            level3: {
                default: '0 0 15px rgba(255, 42, 109, 0.7), 0 0 10px rgba(5, 217, 232, 0.5), 0 0 5px rgba(118, 120, 237, 0.3)',
                hover: '0 0 20px rgba(255, 42, 109, 0.9), 0 0 15px rgba(5, 217, 232, 0.7), 0 0 10px rgba(118, 120, 237, 0.5)'
            }
        },

        borderRadius: {
            sm: '0',
            md: '0.25rem',
            lg: '0.25rem',
            xl: '0.5rem',
            full: '9999px'
        },

        spacing: {
            xs: '0.25rem',
            sm: '0.5rem',
            md: '0.75rem',
            lg: '1rem',
            xl: '1.5rem',
            xxl: '2rem'
        }
    }
};