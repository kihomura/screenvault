export const themeConfig = {
    light: {
        name: 'light',
        colors: {
            primary: '#3d3d3d',
            secondary: '#5a5a5a',
            tertiary: '#7c7c7c',
            highlight: '#AECA5FFF',
            'primary-dark': '#212121',
            'primary-rgb': '61, 61, 61',

            background: {
                base: '#ffffff',
                subtle: '#f9f9f9',
                muted: '#f3f3f3'
            },

            text: {
                primary: '#1a1a1a',
                secondary: '#4a4a4a',
                muted: '#6e6e6e'
            },

            accent: {
                success: '#4caf50',
                warning: '#ff9800',
                error: '#f44336',
                info: '#2196f3'
            },

            interactive: {
                hover: '#eeeeee',
                active: '#e0e0e0',
                focus: '#bdbdbd'
            },

            border: {
                light: '#e0e0e0',
                medium: '#bdbdbd',
                dark: '#9e9e9e'
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
            primary: '#a18072',
            secondary: '#8d7365',
            tertiary: '#6d5a4f',
            highlight: '#e68300',
            'primary-dark': '#8d7365',
            'primary-rgb': '161, 128, 114',

            background: {
                base: '#121212',
                subtle: '#1e1e1e',
                muted: '#2c2c2c'
            },

            text: {
                primary: '#e0e0e0',
                secondary: '#c7c7c7',
                muted: '#a0a0a0'
            },

            accent: {
                success: '#66bb6a',
                warning: '#ffa726',
                error: '#ef5350',
                info: '#42a5f5'
            },

            interactive: {
                hover: '#2a2a2a',
                active: '#333333',
                focus: '#424242'
            },

            border: {
                light: '#333333',
                medium: '#424242',
                dark: '#616161'
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
            tertiary: '#ffd900',
            highlight: '#01012b',
            'primary-dark': '#d60062',
            'primary-rgb': '255, 42, 109',

            background: {
                base: '#0c1016',
                subtle: '#141824',
                muted: '#1c2030'
            },

            text: {
                primary: '#f8f8ff',
                secondary: '#05d9e8',
                muted: '#b0e6ff'
            },

            accent: {
                success: '#6df570',
                warning: '#ffb627',
                error: '#ff3a5e',
                info: '#5be1ff'
            },

            interactive: {
                hover: 'rgba(5, 217, 232, 0.15)',
                active: 'rgba(255, 42, 109, 0.15)',
                focus: 'rgba(255, 217, 0, 0.15)'
            },

            border: {
                light: '#05d9e8',
                medium: '#ff2a6d',
                dark: '#ffd900'
            }
        },

        typography: {
            fontFamily: {
                primary: "'Inter', 'Helvetica Neue', Arial, sans-serif",
                secondary: "'Rajdhani', sans-serif",
                title: "'Orbitron', sans-serif",
                button: "'Rajdhani', sans-serif",
                mono: "'Fira Code', monospace"
            },
            fontSize: {
                xs: '0.75rem',
                sm: '0.875rem',
                base: '1rem',
                lg: '1.125rem',
                xl: '1.25rem',
                xxl: '1.75rem'
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
                hover: '0 0 10px rgba(5, 217, 232, 0.8)'
            },
            level2: {
                default: '0 0 10px rgba(255, 42, 109, 0.5), 0 0 5px rgba(5, 217, 232, 0.3)',
                hover: '0 0 15px rgba(255, 42, 109, 0.7), 0 0 10px rgba(5, 217, 232, 0.5)'
            },
            level3: {
                default: '0 0 15px rgba(255, 42, 109, 0.6), 0 0 10px rgba(5, 217, 232, 0.4), 0 0 5px rgba(255, 217, 0, 0.2)',
                hover: '0 0 20px rgba(255, 42, 109, 0.8), 0 0 15px rgba(5, 217, 232, 0.6), 0 0 10px rgba(255, 217, 0, 0.4)'
            }
        },

        borderRadius: {
            sm: '0',
            md: '0',
            lg: '0',
            xl: '0.25rem',
            full: '9999px'
        },

        spacing: {
            xs: '0.25rem',
            sm: '0.5rem',
            md: '0.75rem',
            lg: '1rem',
            xl: '1.5rem',
            xxl: '2rem'
        },

        effects: {
            glitch: true,
            scanlines: true,
            neon: true,
            noise: true,
            clipPath: 'polygon(0 0, 100% 0, 100% 85%, 98% 100%, 0 100%)'
        }
    }
};