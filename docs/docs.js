// docs.js - Handles navigation and dynamic code sample loading

document.addEventListener('DOMContentLoaded', function () {
  // Mobile nav toggle
  const navToggle = document.getElementById('nav-toggle');
  const navList = document.querySelector('.nav-list');
  if (navToggle && navList) {
    navToggle.addEventListener('click', () => {
      navList.classList.toggle('open');
    });
  }

  // Dropdown accessibility (toggle on click for mobile)
  document.querySelectorAll('.dropdown > .dropbtn').forEach(btn => {
    btn.addEventListener('click', function (e) {
      e.preventDefault();
      const parent = btn.parentElement;
      parent.classList.toggle('open');
      // Close others
      document.querySelectorAll('.dropdown').forEach(d => {
        if (d !== parent) d.classList.remove('open');
      });
    });
  });

  // Dynamic code sample loading
  document.querySelectorAll('.class-link').forEach(link => {
    link.addEventListener('click', function (e) {
      e.preventDefault();
      const className = link.getAttribute('data-class');
      showClassSample(className);
    });
  });

  // Optionally, show a default class sample
  // showClassSample('Rule');

  // Sidebar logic for mobile
  const sidebar = document.getElementById('sidebar-nav');
  const sidebarToggle = document.getElementById('sidebar-toggle');
  const sidebarOverlay = document.getElementById('sidebar-overlay');

  function openSidebar() {
    sidebar.classList.add('open');
    sidebarOverlay.classList.add('open');
  }
  function closeSidebar() {
    sidebar.classList.remove('open');
    sidebarOverlay.classList.remove('open');
  }
  if (sidebarToggle && sidebar && sidebarOverlay) {
    sidebarToggle.addEventListener('click', openSidebar);
    sidebarOverlay.addEventListener('click', closeSidebar);
  }
  // Close sidebar on nav click (mobile UX)
  document.querySelectorAll('.sidebar-nav .class-link, .sidebar-nav .nav-list > li > a').forEach(link => {
    link.addEventListener('click', closeSidebar);
  });
  // Dropdown toggle for sidebar
  sidebar.querySelectorAll('.dropdown > .dropbtn').forEach(btn => {
    btn.addEventListener('click', function (e) {
      e.preventDefault();
      const parent = btn.parentElement;
      parent.classList.toggle('open');
      // Close others
      sidebar.querySelectorAll('.dropdown').forEach(d => {
        if (d !== parent) d.classList.remove('open');
      });
    });
  });
});

function showClassSample(className) {
  const sampleArea = document.getElementById('class-sample');
  const titleArea = document.getElementById('class-sample-title');
  if (!sampleArea || !titleArea) return;
  titleArea.textContent = className;
  sampleArea.textContent = 'Loading...';

  // Map class names to file paths (update as needed)
  const classToPath = {
    'Rule': '../../src/main/java/com/github/sneakytowelsuit/purerules/engine/Rule.java',
    'RuleGroup': '../../src/main/java/com/github/sneakytowelsuit/purerules/engine/RuleGroup.java',
    'EqualsOperator': '../../src/main/java/com/github/sneakytowelsuit/purerules/operators/EqualsOperator.java',
    'GreaterThanOperator': '../../src/main/java/com/github/sneakytowelsuit/purerules/operators/GreaterThanOperator.java',
    'LessThanOperator': '../../src/main/java/com/github/sneakytowelsuit/purerules/operators/LessThanOperator.java',
    'NotEqualsOperator': '../../src/main/java/com/github/sneakytowelsuit/purerules/operators/NotEqualsOperator.java',
    'StringContainsCaseInsensitiveOperator': '../../src/main/java/com/github/sneakytowelsuit/purerules/operators/StringContainsCaseInsensitiveOperator.java',
    'StringEndsWithOperator': '../../src/main/java/com/github/sneakytowelsuit/purerules/operators/StringEndsWithOperator.java',
    'StringEqualsIgnoreCaseOperator': '../../src/main/java/com/github/sneakytowelsuit/purerules/operators/StringEqualsIgnoreCaseOperator.java',
    'StringStartsWithOperator': '../../src/main/java/com/github/sneakytowelsuit/purerules/operators/StringStartsWithOperator.java',
    'RuleGroupSerde': '../../src/main/java/com/github/sneakytowelsuit/purerules/serialization/RuleGroupSerde.java',
    'RuleGroupSerializer': '../../src/main/java/com/github/sneakytowelsuit/purerules/serialization/RuleGroupSerializer.java',
    'RuleSerializer': '../../src/main/java/com/github/sneakytowelsuit/purerules/serialization/RuleSerializer.java',
  };
  const filePath = classToPath[className];
  if (!filePath) {
    sampleArea.textContent = 'No code sample available.';
    return;
  }

  // Use fetch only for files accessible via HTTP (docs/), fallback to error for src/
  fetch(filePath)
    .then(r => r.ok ? r.text() : Promise.reject('Not found'))
    .then(text => {
      // Escape HTML for safe rendering
      const escapeHtml = (str) => str.replace(/[&<>"']/g, function(m) {
        return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[m]);
      });
      const lines = text.split('\n').slice(0, 40).join('\n');
      sampleArea.innerHTML = '<code>' + escapeHtml(lines) + (text.split('\n').length > 40 ? '\n...' : '') + '</code>';
    })
    .catch(() => {
      sampleArea.textContent = 'Unable to load code sample. (Local files may not be accessible in browser preview)';
    });
}
