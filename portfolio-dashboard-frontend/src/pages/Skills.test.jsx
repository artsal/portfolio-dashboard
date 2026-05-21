import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import Skills from './Skills';
import { fetchSkills } from '../api/skillService';
import { toast } from 'react-toastify';

vi.mock('../api/skillService', () => ({
  fetchSkills: vi.fn(),
  addSkill: vi.fn(),
  updateSkill: vi.fn(),
  deleteSkill: vi.fn(),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    warn: vi.fn(),
    error: vi.fn(),
    info: vi.fn(),
  },
}));

vi.mock('../components/SkillModal', () => ({
  default: ({ open, skill }) =>
    open ? <div role="dialog">{skill ? `Editing ${skill.name}` : 'New skill form'}</div> : null,
}));

vi.mock('../components/ConfirmDialog', () => ({
  default: ({ open, title, message }) =>
    open ? (
      <div role="dialog">
        <h2>{title}</h2>
        <p>{message}</p>
      </div>
    ) : null,
}));

const skills = [
  {
    id: 1,
    name: 'React',
    category: 'Frontend',
    proficiency: 90,
    yearsOfExperience: 4,
    lastUsed: 'Currently using',
    color: 'bg-blue-500',
  },
  {
    id: 2,
    name: 'Spring Boot',
    category: 'Backend',
    proficiency: 85,
    yearsOfExperience: 3,
    lastUsed: '2026-01-01',
    color: 'bg-green-500',
  },
];

describe('Skills page', () => {
  it('groups skills by category and toggles category visibility', async () => {
    const user = userEvent.setup();
    fetchSkills.mockResolvedValueOnce(skills);

    render(<Skills />);

    expect(screen.getByText(/loading skills/i)).toBeInTheDocument();
    expect(await screen.findByText('Frontend')).toBeInTheDocument();
    expect(screen.getByText('Backend')).toBeInTheDocument();
    expect(screen.getByText('React')).toBeInTheDocument();
    expect(screen.getByText('Spring Boot')).toBeInTheDocument();

    const frontendSection = screen.getByText('Frontend').closest('.rounded-2xl');
    await user.click(within(frontendSection).getByText(/collapse/i));

    expect(screen.queryByText('React')).not.toBeInTheDocument();
    expect(screen.getByText('Spring Boot')).toBeInTheDocument();
  });

  it('opens add, edit, and delete UI from visible controls', async () => {
    const user = userEvent.setup();
    fetchSkills.mockResolvedValueOnce(skills);

    render(<Skills />);

    await screen.findByText('React');
    await user.click(screen.getByRole('button', { name: /\+ new skill/i }));
    expect(screen.getByText('New skill form')).toBeInTheDocument();

    const frontendSection = screen.getByText('Frontend').closest('.rounded-2xl');
    await user.click(within(frontendSection).getByRole('button', { name: /edit/i }));
    expect(screen.getByRole('dialog')).toHaveTextContent('Editing React');

    await user.click(within(frontendSection).getByRole('button', { name: /delete/i }));
    expect(screen.getAllByRole('dialog').at(-1)).toHaveTextContent('Delete Skill');
    expect(screen.getByText(/delete "react"/i)).toBeInTheDocument();
  });

  it('shows cached skills and disables editing when the backend is unavailable', async () => {
    const user = userEvent.setup();
    vi.spyOn(console, 'error').mockImplementation(() => {});
    localStorage.setItem('cache:skills', JSON.stringify({ data: skills }));
    fetchSkills.mockRejectedValueOnce(new Error('offline'));

    render(<Skills />);

    expect(await screen.findByText(/offline mode/i)).toBeInTheDocument();
    const frontendSection = screen.getByText('Frontend').closest('.rounded-2xl');
    await user.click(within(frontendSection).getByText(/expand/i));
    expect(screen.getByText('React')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: /\+ new skill/i }));

    expect(toast.warn).toHaveBeenCalledWith("Can't add skills while offline");
    expect(screen.queryByText('New skill form')).not.toBeInTheDocument();
  });
});
