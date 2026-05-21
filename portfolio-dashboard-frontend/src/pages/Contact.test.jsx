import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import Contact from './Contact';
import { toast } from 'react-toastify';

vi.mock('react-toastify', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
    warn: vi.fn(),
  },
}));

describe('Contact page', () => {
  beforeEach(() => {
    globalThis.fetch = vi.fn();
  });

  it('validates required fields before submitting', async () => {
    const user = userEvent.setup();

    render(<Contact />);

    await user.click(screen.getByRole('button', { name: /send message/i }));

    expect(toast.error).toHaveBeenCalledWith('Please fill in all fields.', {
      theme: 'colored',
    });
    expect(fetch).not.toHaveBeenCalled();
  });

  it('submits a valid contact message and clears the form', async () => {
    const user = userEvent.setup();
    fetch.mockResolvedValueOnce({ ok: true });

    render(<Contact />);

    await user.type(screen.getByLabelText(/name/i), 'Jane Recruiter');
    await user.type(screen.getByLabelText(/email/i), 'jane@example.com');
    await user.type(screen.getByLabelText(/message/i), 'Can we talk?');
    await user.click(screen.getByRole('button', { name: /send message/i }));

    await waitFor(() => expect(toast.success).toHaveBeenCalledWith('Message sent successfully!', {
      theme: 'colored',
    }));
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:1907/pdbapp/api/contact',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({
          name: 'Jane Recruiter',
          email: 'jane@example.com',
          message: 'Can we talk?',
        }),
      }),
    );
    expect(screen.getByLabelText(/name/i)).toHaveValue('');
  });

  it('shows the direct email fallback when submission fails', async () => {
    const user = userEvent.setup();
    vi.spyOn(console, 'error').mockImplementation(() => {});
    fetch.mockRejectedValueOnce(new Error('network down'));

    render(<Contact />);

    await user.type(screen.getByLabelText(/name/i), 'Jane Recruiter');
    await user.type(screen.getByLabelText(/email/i), 'jane@example.com');
    await user.type(screen.getByLabelText(/message/i), 'Can we talk?');
    await user.click(screen.getByRole('button', { name: /send message/i }));

    expect(await screen.findByText(/service temporarily unavailable/i)).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /arthur\.sj\.salla@gmail\.com/i })).toHaveAttribute(
      'href',
      'mailto:arthur.sj.salla@gmail.com',
    );
    expect(toast.warn).toHaveBeenCalledWith('Backend unreachable. Please use the email link below.', {
      theme: 'colored',
    });
  });
});
