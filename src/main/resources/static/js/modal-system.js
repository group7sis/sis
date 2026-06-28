/**
 * Reusable Modal System
 * Supports: open, close, data binding, keyboard navigation, focus management
 * Works with any backend API - no page-specific code needed
 */

const ModalSystem = (() => {
  const state = {
    currentModal: null,
    history: [],
    maxHistory: 50
  };

  function create(config = {}) {
    return {
      id: config.id || 'modal-default',
      data: config.data || {},
      options: config.options || {},
      metadata: {
        createdAt: Date.now(),
        version: '2.0.0'
      }
    };
  }

  function open(modalId, data = {}) {
    const modal = state.history.find(m => m.id === modalId);
    if (modal) {
      modal.isOpen = true;
      modal.lastAccessed = Date.now();
      state.currentModal = modal;
      render(modal);
      bindEvents(modal);
      focusFirstField(modal);
      return modal;
    }
    const newModal = create({ id: modalId, data });
    state.history.push(newModal);
    state.currentModal = newModal;
    render(newModal);
    bindEvents(newModal);
    return newModal;
  }

  function close(modalId) {
    const modal = modalId ? state.history.find(m => m.id === modalId) : state.currentModal;
    if (modal) {
      modal.isOpen = false;
      state.currentModal = null;
      cleanup(modal);
    }
  }

  function render(modal) {
    const container = document.getElementById('modal-container');
    if (!container) return;
    container.innerHTML = generateHTML(modal);
    container.classList.add('active');
  }

  function generateHTML(modal) {
    return `
      <div class="modal-overlay" data-modal-id="${modal.id}">
        <div class="modal" role="dialog" aria-modal="true">
          <div class="modal-header">
            <h2>${modal.data.title || 'Modal'}</h2>
            <button class="modal-close" aria-label="Close">&times;</button>
          </div>
          <div class="modal-body">
            ${modal.data.content || ''}
          </div>
        </div>
      </div>
    `;
  }

  function bindEvents(modal) {
    const closeBtn = document.querySelector(`[data-modal-id="${modal.id}"] .modal-close`);
    const overlay = document.querySelector(`[data-modal-id="${modal.id}"]`);
    
    closeBtn?.addEventListener('click', () => close(modal.id));
    overlay?.addEventListener('click', (e) => {
      if (e.target === overlay) close(modal.id);
    });
    
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && state.currentModal?.id === modal.id) {
        close(modal.id);
      }
    });
  }

  function focusFirstField(modal) {
    const firstInput = document.querySelector(`[data-modal-id="${modal.id}"] input, [data-modal-id="${modal.id}"] select, [data-modal-id="${modal.id}"] textarea`);
    firstInput?.focus();
  }

  function cleanup(modal) {
    const container = document.getElementById('modal-container');
    if (container) container.innerHTML = '';
  }

  return { open, close, create, state };
})();

// Export for use
window.ModalSystem = ModalSystem;