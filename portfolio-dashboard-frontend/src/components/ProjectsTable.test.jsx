import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import ProjectsTable from './ProjectsTable';
import { fetchProjectById } from '../api/projectService';

vi.mock('../api/projectService', () => ({
  fetchProjectById: vi.fn(),
}));

vi.mock('react-toastify', () => ({
  toast: {
    error: vi.fn(),
  },
}));

const projects = [
  {
    id: 1,
    title: 'Portfolio Dashboard',
    description: 'Full-stack portfolio app',
    techStack: 'React,Spring Boot,MySQL',
    status: 'Active',
    startDate: '2025-01-01',
    endDate: '',
    githubLink: 'https://github.com/artsal/portfolio-dashboard',
  },
  {
    id: 2,
    title: 'Legacy Migration',
    description: 'Modernized an older system',
    techStack: 'Java,SQL',
    status: 'Completed',
    startDate: '2024-02-01',
    endDate: '2024-09-01',
  },
];

describe('ProjectsTable', () => {
  it('renders project details and filters by search text', async () => {
    const user = userEvent.setup();

    render(<ProjectsTable data={projects} />);

    expect(screen.getByText('Portfolio Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Legacy Migration')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /view on github/i })).toHaveAttribute(
      'href',
      'https://github.com/artsal/portfolio-dashboard',
    );

    await user.type(screen.getByPlaceholderText(/search projects/i), 'legacy');

    expect(screen.queryByText('Portfolio Dashboard')).not.toBeInTheDocument();
    expect(screen.getByText('Legacy Migration')).toBeInTheDocument();
    expect(screen.getByText('1 project(s)')).toBeInTheDocument();
  });

  it('calls edit and delete handlers from the row action buttons', async () => {
    const user = userEvent.setup();
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    render(<ProjectsTable data={projects} onEdit={onEdit} onDelete={onDelete} />);

    const firstRow = screen.getByText('Portfolio Dashboard').closest('tr');
    await user.click(within(firstRow).getByRole('button', { name: /edit/i }));
    await user.click(within(firstRow).getByRole('button', { name: /delete/i }));

    expect(onEdit).toHaveBeenCalledWith(projects[0]);
    expect(onDelete).toHaveBeenCalledWith(1);
  });

  it('opens the project details modal after loading project details', async () => {
    const user = userEvent.setup();
    fetchProjectById.mockResolvedValueOnce({
      ...projects[0],
      description: 'Detailed project description',
    });

    render(<ProjectsTable data={projects} />);

    const firstRow = screen.getByText('Portfolio Dashboard').closest('tr');
    await user.click(within(firstRow).getByRole('button', { name: /view/i }));

    expect(fetchProjectById).toHaveBeenCalledWith(1);
    await waitFor(() => expect(screen.getAllByText('Portfolio Dashboard').length).toBeGreaterThan(1));
    expect(screen.getByText('Detailed project description')).toBeInTheDocument();
  });
});
