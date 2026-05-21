import { render, screen, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import Overview from './Overview';
import { apiFetch } from '../api/api';

vi.mock('../api/api', () => ({
  apiFetch: vi.fn(),
}));

vi.mock('../charts/ProjectsBarChart', () => ({
  default: () => <div>Projects chart</div>,
}));

vi.mock('../charts/SkillsPieChart', () => ({
  default: () => <div>Skills chart</div>,
}));

describe('Overview page', () => {
  it('renders dashboard stats returned by the API', async () => {
    apiFetch.mockResolvedValueOnce({
      fromCache: false,
      data: {
        projects: { latest: 'Portfolio Dashboard', count: 4 },
        skills: { top: ['React', 'Spring Boot'], count: 8 },
        experience: { years: 16 },
        certifications: { latest: 'AWS Cloud Practitioner', count: 2 },
      },
    });

    render(<Overview />);

    expect(screen.getByText(/loading overview/i)).toBeInTheDocument();
    expect(await screen.findByText('Portfolio Dashboard')).toBeInTheDocument();
    expect(screen.getByText('React, Spring Boot')).toBeInTheDocument();
    expect(screen.getByText('16 Years')).toBeInTheDocument();
    expect(screen.getByText('AWS Cloud Practitioner')).toBeInTheDocument();
    expect(screen.getByText('Projects chart')).toBeInTheDocument();
    expect(screen.getByText('Skills chart')).toBeInTheDocument();
  });

  it('falls back to demo data when the backend is unreachable', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => {});
    apiFetch.mockRejectedValueOnce(new Error('offline'));

    render(<Overview />);

    expect(await screen.findByText(/backend unreachable, showing demo data/i)).toBeInTheDocument();
    expect(screen.getByText('Demo Project')).toBeInTheDocument();
    expect(screen.getByText('React, Java, MySQL')).toBeInTheDocument();

    await waitFor(() => expect(screen.queryByText(/loading overview/i)).not.toBeInTheDocument());
  });
});
