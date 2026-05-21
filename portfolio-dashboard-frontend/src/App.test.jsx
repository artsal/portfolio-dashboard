import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import App from './App';

vi.mock('./pages/Overview', () => ({
  default: () => <h2>Overview Page</h2>,
}));

vi.mock('./pages/Projects', () => ({
  default: () => <h2>Projects Page</h2>,
}));

vi.mock('./pages/Skills', () => ({
  default: () => <h2>Skills Page</h2>,
}));

vi.mock('./pages/Contact', () => ({
  default: () => <h2>Contact Page</h2>,
}));

describe('App routing', () => {
  beforeEach(() => {
    window.history.pushState({}, '', '/');
  });

  it('shows the overview page by default and navigates with sidebar links', async () => {
    const user = userEvent.setup();

    render(<App />);

    expect(screen.getByRole('heading', { name: /dashboard/i })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /overview page/i })).toBeInTheDocument();

    await user.click(screen.getByRole('link', { name: /projects/i }));
    expect(screen.getByRole('heading', { name: /projects page/i })).toBeInTheDocument();

    await user.click(screen.getByRole('link', { name: /skills/i }));
    expect(screen.getByRole('heading', { name: /skills page/i })).toBeInTheDocument();

    await user.click(screen.getByRole('link', { name: /contact/i }));
    expect(screen.getByRole('heading', { name: /contact page/i })).toBeInTheDocument();
  });
});
