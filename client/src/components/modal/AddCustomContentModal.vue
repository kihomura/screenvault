<template>
  <div class="modal-backdrop" v-if="isOpen" @click.self="closeModal">
    <div class="modal-container">
      <div class="modal-header">
        <h2 class="modal-title">Add Custom Content</h2>
        <button class="close-button" @click="closeModal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>

      <div class="modal-body">
        <div class="form-container">
          <div class="form-section">
            <h3 class="section-title">Basic Information</h3>

            <div class="form-row">
              <div class="form-group">
                <label for="title" class="required">Title</label>
                <input
                    type="text"
                    id="title"
                    v-model="formData.title"
                    class="form-control"
                    placeholder="Enter main title"
                    required
                />
                <div class="form-feedback" v-if="titleError">Please enter a title</div>
              </div>

              <div class="form-group">
                <label for="otherTitle">Alternative Title</label>
                <input
                    type="text"
                    id="otherTitle"
                    v-model="formData.otherTitle"
                    class="form-control"
                    placeholder="Other title or original name"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="releaseDate">Release Date</label>
                <input
                    type="date"
                    id="releaseDate"
                    v-model="formData.releaseDate"
                    class="form-control"
                    :max="currentDate"
                />
              </div>

              <div class="form-group country-select">
                <label for="country">Country</label>
                <div class="autocomplete-container">
                  <input
                      type="text"
                      id="country"
                      v-model="countryInput"
                      @input="searchCountries"
                      @focus="isFocusedCountry = true"
                      @blur="handleCountryBlur"
                      class="form-control"
                      placeholder="Country code (e.g. US, JP)"
                      maxlength="2"
                  />
                  <div class="country-code" v-if="formData.country">{{ formData.country }}</div>
                  <div class="suggestions-container" v-if="isFocusedCountry && filteredCountries.length > 0">
                    <ul class="suggestions-list">
                      <li
                          v-for="country in filteredCountries"
                          :key="country.code"
                          @mousedown="selectCountry(country)"
                          class="suggestion-item"
                      >
                        <strong>{{ country.code }}</strong> ({{ country.name }})
                      </li>
                    </ul>
                  </div>
                </div>
                <div class="form-hint">Enter 2-letter ISO country code</div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group language-select">
                <label for="language">Language</label>
                <div class="autocomplete-container">
                  <input
                      type="text"
                      id="language"
                      v-model="languageInput"
                      @input="searchLanguages"
                      @focus="isFocusedLanguage = true"
                      @blur="handleLanguageBlur"
                      class="form-control"
                      placeholder="Language code (e.g. EN, FR)"
                      maxlength="2"
                  />
                  <div class="language-code" v-if="formData.language">{{ formData.language }}</div>
                  <div class="suggestions-container" v-if="isFocusedLanguage && filteredLanguages.length > 0">
                    <ul class="suggestions-list">
                      <li
                          v-for="language in filteredLanguages"
                          :key="language.code"
                          @mousedown="selectLanguage(language)"
                          class="suggestion-item"
                      >
                        <strong>{{ language.code }}</strong> ({{ language.name }})
                      </li>
                    </ul>
                  </div>
                </div>
                <div class="form-hint">Enter 2-letter ISO language code</div>
              </div>

              <div class="form-group">
                <label for="image">Image URL</label>
                <input
                    type="text"
                    id="image"
                    v-model="formData.image"
                    class="form-control"
                    placeholder="Poster or cover image URL"
                />
                <div class="image-preview" v-if="formData.image">
                  <img :src="formData.image" @error="handleImageError" alt="Preview" />
                  <div class="image-error" v-if="imageError">Invalid image URL</div>
                </div>
              </div>
            </div>
          </div>

          <div class="form-section">
            <h3 class="section-title">Classification</h3>

            <div class="form-row">
              <div class="form-group">
                <label for="category" class="required">Category</label>
                <select
                    id="category"
                    v-model="formData.category"
                    class="form-control"
                    required
                >
                  <option value="">Select a category</option>
                  <option value="MOVIE">Movie</option>
                  <option value="TV_SHOW">TV Show</option>
                </select>
                <div class="form-feedback" v-if="categoryError">Please select a category</div>
              </div>

              <div class="form-group">
                <label for="genre" class="required">Genre</label>
                <select
                    id="genre"
                    v-model="formData.genre"
                    class="form-control"
                    required
                >
                  <option value="">Select a genre</option>
                  <option v-for="genre in genres" :key="genre.value" :value="genre.value">
                    {{ genre.name }}
                  </option>
                </select>
                <div class="form-feedback" v-if="genreError">Please select a genre</div>
              </div>
            </div>
          </div>

          <div class="form-section">
            <h3 class="section-title">Description</h3>

            <div class="form-group">
              <textarea
                  id="description"
                  v-model="formData.description"
                  class="form-control"
                  rows="4"
                  placeholder="Enter a brief description of the content..."
              ></textarea>
            </div>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <div class="action-buttons">
          <main-btn type="secondary" class="cancel-button" @click="closeModal">Cancel</main-btn>
          <div class="save-buttons">
            <main-btn type="primary" class="save-button" @click="saveContent(false)" :disabled="!isFormValid">Save</main-btn>
            <main-btn type="highlight" class="save-add-button" @click="saveContent(true)" :disabled="!isFormValid">
              Save & Add Recording
            </main-btn>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MainBtn from "../buttons/MainBtn.vue";

export default {
  name: 'AddCustomContentModal',
  components: {MainBtn},
  props: {
    isOpen: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      currentDate: new Date().toISOString().split('T')[0],
      placeholderImage: '/placeholder-poster.jpg',
      imageError: false,
      titleError: false,
      categoryError: false,
      genreError: false,
      formData: {
        title: '',
        otherTitle: '',
        country: '',
        language: '',
        description: '',
        image: '',
        releaseDate: '',
        genre: '',
        category: '',
        sourceType: 'CUSTOM_DATA'
      },
      countryInput: '',
      languageInput: '',
      isFocusedCountry: false,
      isFocusedLanguage: false,
      // Common countries with their 2-letter codes
      countries: [
        { code: 'US', name: 'United States' },
        { code: 'GB', name: 'United Kingdom' },
        { code: 'CA', name: 'Canada' },
        { code: 'AU', name: 'Australia' },
        { code: 'FR', name: 'France' },
        { code: 'DE', name: 'Germany' },
        { code: 'JP', name: 'Japan' },
        { code: 'KR', name: 'South Korea' },
        { code: 'CN', name: 'China' },
        { code: 'IN', name: 'India' },
        { code: 'BR', name: 'Brazil' },
        { code: 'MX', name: 'Mexico' },
        { code: 'ES', name: 'Spain' },
        { code: 'IT', name: 'Italy' },
        { code: 'RU', name: 'Russia' },
        { code: 'NZ', name: 'New Zealand' },
        { code: 'SE', name: 'Sweden' },
        { code: 'NO', name: 'Norway' },
        { code: 'FI', name: 'Finland' },
        { code: 'DK', name: 'Denmark' }
      ],
      // Common languages with their 2-letter codes
      languages: [
        { code: 'EN', name: 'English' },
        { code: 'ES', name: 'Spanish' },
        { code: 'FR', name: 'French' },
        { code: 'DE', name: 'German' },
        { code: 'IT', name: 'Italian' },
        { code: 'PT', name: 'Portuguese' },
        { code: 'RU', name: 'Russian' },
        { code: 'JA', name: 'Japanese' },
        { code: 'ZH', name: 'Chinese' },
        { code: 'KO', name: 'Korean' },
        { code: 'AR', name: 'Arabic' },
        { code: 'HI', name: 'Hindi' },
        { code: 'SV', name: 'Swedish' },
        { code: 'NL', name: 'Dutch' },
        { code: 'EL', name: 'Greek' },
        { code: 'HE', name: 'Hebrew' }
      ],
      filteredCountries: [],
      filteredLanguages: [],
      genres: [
        { value: 'ACTION', name: 'Action' },
        { value: 'ADVENTURE', name: 'Adventure' },
        { value: 'ANIMATION', name: 'Animation' },
        { value: 'COMEDY', name: 'Comedy' },
        { value: 'CRIME', name: 'Crime' },
        { value: 'DOCUMENTARY', name: 'Documentary' },
        { value: 'DRAMA', name: 'Drama' },
        { value: 'FAMILY', name: 'Family' },
        { value: 'FANTASY', name: 'Fantasy' },
        { value: 'HISTORY', name: 'History' },
        { value: 'HORROR', name: 'Horror' },
        { value: 'MUSIC', name: 'Music' },
        { value: 'MYSTERY', name: 'Mystery' },
        { value: 'ROMANCE', name: 'Romance' },
        { value: 'SCI_FI_FANTASY', name: 'Sci-Fi & Fantasy' },
        { value: 'TV_MOVIE', name: 'TV Movie' },
        { value: 'THRILLER', name: 'Thriller' },
        { value: 'WAR', name: 'War' },
        { value: 'WESTERN', name: 'Western' },
        { value: 'ACTION_ADVENTURE', name: 'Action & Adventure' },
        { value: 'KIDS', name: 'Kids' },
        { value: 'NEWS', name: 'News' },
        { value: 'REALITY', name: 'Reality' },
        { value: 'SOAP', name: 'Soap' },
        { value: 'TALK', name: 'Talk' },
        { value: 'WAR_POLITICS', name: 'War & Politics' },
        { value: 'SCIENCE_FICTION', name: 'Science Fiction' }
      ]
    };
  },
  computed: {
    isFormValid() {
      this.titleError = !this.formData.title;
      this.categoryError = !this.formData.category;
      this.genreError = !this.formData.genre;

      return this.formData.title &&
          this.formData.category &&
          this.formData.genre &&
          !this.imageError;
    }
  },
  methods: {
    closeModal() {
      this.resetForm();
      this.$emit('close');
    },

    resetForm() {
      this.formData = {
        title: '',
        otherTitle: '',
        country: '',
        language: '',
        description: '',
        image: '',
        releaseDate: '',
        genre: '',
        category: '',
        sourceType: 'CUSTOM_DATA'
      };
      this.countryInput = '';
      this.languageInput = '';
      this.imageError = false;
      this.titleError = false;
      this.categoryError = false;
      this.genreError = false;
      this.filteredCountries = [];
      this.filteredLanguages = [];
    },

    searchCountries() {
      const query = this.countryInput.toUpperCase();
      this.formData.country = query.length === 2 ? query : '';

      if (query) {
        this.filteredCountries = this.countries.filter(country =>
            country.code.includes(query) ||
            country.name.toUpperCase().includes(query)
        ).slice(0, 5);
      } else {
        this.filteredCountries = [];
      }
    },

    searchLanguages() {
      const query = this.languageInput.toUpperCase();
      this.formData.language = query.length === 2 ? query : '';

      if (query) {
        this.filteredLanguages = this.languages.filter(language =>
            language.code.includes(query) ||
            language.name.toUpperCase().includes(query)
        ).slice(0, 5);
      } else {
        this.filteredLanguages = [];
      }
    },

    selectCountry(country) {
      this.formData.country = country.code;
      this.countryInput = country.code;
      this.filteredCountries = [];
    },

    selectLanguage(language) {
      this.formData.language = language.code;
      this.languageInput = language.code;
      this.filteredLanguages = [];
    },

    handleCountryBlur() {
      setTimeout(() => {
        this.isFocusedCountry = false;
        if (this.countryInput.length === 2) {
          this.countryInput = this.countryInput.toUpperCase();
          this.formData.country = this.countryInput;
        }
      }, 200);
    },

    handleLanguageBlur() {
      setTimeout(() => {
        this.isFocusedLanguage = false;
        if (this.languageInput.length === 2) {
          this.languageInput = this.languageInput.toUpperCase();
          this.formData.language = this.languageInput;
        }
      }, 200);
    },

    handleImageError() {
      this.imageError = true;
    },

    validateForm() {
      this.titleError = !this.formData.title;
      this.categoryError = !this.formData.category;
      this.genreError = !this.formData.genre;

      return !this.titleError && !this.categoryError && !this.genreError;
    },

    async saveContent(addRecording) {
      if (!this.validateForm()) {
        return;
      }
      try {
        const contentData = {
          title: this.formData.title.trim(),
          otherTitle: this.formData.otherTitle.trim(),
          country: this.formData.country.trim(),
          language: this.formData.language.trim(),
          description: this.formData.description.trim(),
          image: this.formData.image.trim(),
          releaseDate: this.formData.releaseDate || null,
          genre: this.formData.genre,
          category: this.formData.category,
          sourceType: 'CUSTOM_DATA'
        };

        const response = await this.$http.post('/content', contentData);

        if (response && response.data.data) {
          const newContent = response.data.data;

          // keep adding a new watching recording
          if (addRecording) {
            this.$emit('save', newContent);
          } else {
            // Save only
            this.$emit('close');
          }
          this.resetForm();
        }
      } catch (error) {
        console.error('Error saving custom content:', error);
      }
    },
  }
};
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(var(--primary-dark-rgb), 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
  backdrop-filter: blur(4px);
}

.modal-container {
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level3-default);
  display: flex;
  flex-direction: column;
  animation: modal-appear 0.3s ease-out;
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border-light);
  background-color: var(--background-subtle);
  border-top-left-radius: var(--border-radius-lg);
  border-top-right-radius: var(--border-radius-lg);
}

.modal-title {
  margin: 0;
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-semibold);
  font-size: var(--font-fontSize-xxl);
  color: var(--primary);
}

.close-button {
  background: transparent;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: var(--border-radius-full);
  color: var(--text-secondary);
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-button:hover {
  background-color: var(--interactive-hover);
  color: var(--primary-dark);
}

.modal-body {
  padding: var(--spacing-xl);
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: var(--tertiary) var(--background-subtle);
  flex: 1;
}

.modal-body::-webkit-scrollbar {
  width: 5px;
}

.modal-body::-webkit-scrollbar-track {
  background: var(--background-subtle);
  border-radius: var(--border-radius-full);
}

.modal-body::-webkit-scrollbar-thumb {
  background-color: var(--tertiary);
  border-radius: var(--border-radius-full);
}

/* Form Container */
.form-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

/* Form Sections */
.form-section {
  padding: var(--spacing-lg);
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-md);
  border-left: 4px solid var(--primary);
  box-shadow: var(--shadow-level1-default);
  transition: all 0.2s ease;
}

.form-section:hover {
  box-shadow: var(--shadow-level1-hover);
}

.section-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-medium);
  color: var(--primary);
  margin-top: 0;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-xs);
  border-bottom: 1px dashed var(--border-light);
}

/* Form Rows */
.form-row {
  display: flex;
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-lg);
  flex-wrap: wrap;
}

.form-row:last-child {
  margin-bottom: 0;
}

/* Form Groups */
.form-group {
  flex: 1;
  min-width: 250px;
  position: relative;
  margin-bottom: var(--spacing-md);
}

/* Form Labels */
label {
  display: block;
  margin-bottom: var(--spacing-xs);
  font-family: var(--font-fontFamily-primary);
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
}

label.required::after {
  content: "*";
  color: var(--accent-error);
  margin-left: 4px;
}

/* Form Controls */
.form-control {
  width: 100%;
  padding: var(--spacing-md);
  font-family: var(--font-fontFamily-primary);
  font-size: var(--font-fontSize-base);
  color: var(--text-primary);
  background-color: var(--background-base);
  border: 1px solid var(--border-medium);
  border-radius: var(--border-radius-md);
  transition: all 0.2s ease;
}

.form-control:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(var(--primary-rgb), 0.15);
}

.form-control::placeholder {
  color: var(--text-muted);
  opacity: 0.7;
}

select.form-control {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%236e6e6e' d='M6 8.825L1.175 4 2.238 2.938 6 6.7 9.763 2.938 10.825 4z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right var(--spacing-lg) center;
  padding-right: var(--spacing-xxl);
}

textarea.form-control {
  resize: vertical;
  min-height: 120px;
}

/* Form Feedback */
.form-feedback {
  position: absolute;
  color: var(--accent-error);
  font-size: var(--font-fontSize-xs);
  margin-top: var(--spacing-xs);
}

.form-hint {
  font-size: var(--font-fontSize-xs);
  color: var(--text-muted);
  margin-top: var(--spacing-xs);
}

/* Autocomplete */
.autocomplete-container {
  position: relative;
}

.country-code,
.language-code {
  position: absolute;
  right: var(--spacing-md);
  top: 50%;
  transform: translateY(-50%);
  background-color: var(--primary);
  color: var(--background-base);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  font-size: var(--font-fontSize-xs);
  font-weight: var(--font-fontWeight-bold);
}

.suggestions-container {
  position: absolute;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
  background-color: var(--background-base);
  border: 1px solid var(--border-medium);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-level2-default);
  z-index: 10;
}

.suggestions-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.suggestion-item {
  padding: var(--spacing-md);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.suggestion-item:hover {
  background-color: var(--interactive-hover);
}

/* Image Preview */
.image-preview {
  margin-top: var(--spacing-md);
  border-radius: var(--border-radius-md);
  overflow: hidden;
  border: 1px solid var(--border-light);
}

.image-preview img {
  width: 100%;
  max-height: 150px;
  object-fit: cover;
}

.image-error {
  color: var(--accent-error);
  background-color: rgba(var(--accent-error-rgb), 0.1);
  padding: var(--spacing-sm);
  font-size: var(--font-fontSize-xs);
}

/* Form Actions */
.form-actions {
  padding: var(--spacing-lg) var(--spacing-xl);
  background-color: var(--background-muted);
  border-top: 1px solid var(--border-light);
  border-bottom-left-radius: var(--border-radius-lg);
  border-bottom-right-radius: var(--border-radius-lg);
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.save-buttons {
  display: flex;
  gap: var(--spacing-md);
}

/* Buttons */
button {
  padding: var(--spacing-md) var(--spacing-xl);
  font-family: var(--font-fontFamily-primary);
  font-weight: var(--font-fontWeight-medium);
  font-size: var(--font-fontSize-base);
  border-radius: var(--border-radius-md);
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-button {
  background-color: var(--background-base);
  color: var(--text-secondary);
  border: 1px solid var(--border-medium);
}

.cancel-button:hover {
  background-color: var(--interactive-hover);
}

.save-button {
  background-color: var(--primary);
  color: white;
}

.save-button:hover {
  background-color: var(--primary-dark);
  box-shadow: var(--shadow-level2-hover);
}

.save-add-button {
  background-color: var(--accent-info);
  color: white;
}

.save-add-button:hover {
  background-color: rgba(var(--accent-info-rgb), 0.85);
  box-shadow: var(--shadow-level2-hover);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .form-group {
    min-width: 100%;
  }

  .action-buttons {
    flex-direction: column-reverse;
    gap: var(--spacing-lg);
  }

  .save-buttons {
    width: 100%;
  }

  .save-button,
  .save-add-button,
  .cancel-button {
    flex: 1;
    text-align: center;
  }
}
</style>