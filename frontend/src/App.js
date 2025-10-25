import React, { useState, useEffect } from 'react';
import './App.css';

const API_BASE_URL = 'http://localhost:8080';

function App() {
  const [notes, setNotes] = useState({ temp: [], db: [] });
  const [formData, setFormData] = useState({ title: '', content: '' });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  // Fetch all notes on component mount
  useEffect(() => {
    fetchAllNotes();
  }, []);

  const fetchAllNotes = async () => {
    try {
      setLoading(true);
      const [tempResponse, dbResponse] = await Promise.all([
        fetch(`${API_BASE_URL}/temp`),
        fetch(`${API_BASE_URL}/notes`)
      ]);

      const tempNotes = await tempResponse.json();
      const dbNotes = await dbResponse.json();

      setNotes({
        temp: Array.isArray(tempNotes) ? tempNotes : [],
        db: Array.isArray(dbNotes) ? dbNotes : []
      });
    } catch (error) {
      showMessage('Error fetching notes: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const saveAsDraft = async () => {
    if (!formData.title || !formData.content) {
      showMessage('Please fill in both title and content');
      return;
    }

    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/temp`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      const result = await response.json();

      if (response.ok) {
        showMessage('Note saved as draft successfully!');
        setFormData({ title: '', content: '' });
        fetchAllNotes();
      } else {
        showMessage('Error: ' + result.error);
      }
    } catch (error) {
      showMessage('Error saving draft: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const submitNote = async () => {
    if (!formData.title || !formData.content) {
      showMessage('Please fill in both title and content');
      return;
    }

    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/notes`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      const result = await response.json();

      if (response.ok) {
        showMessage('Note submitted successfully!');
        setFormData({ title: '', content: '' });
        fetchAllNotes();
      } else {
        showMessage('Error: ' + result.error);
      }
    } catch (error) {
      showMessage('Error submitting note: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const deleteTempNote = async (id) => {
    try {
      const response = await fetch(`${API_BASE_URL}/temp/${id}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        showMessage('Temporary note deleted successfully!');
        fetchAllNotes();
      } else {
        const result = await response.json();
        showMessage('Error: ' + result.error);
      }
    } catch (error) {
      showMessage('Error deleting note: ' + error.message);
    }
  };

  const deleteDbNote = async (id) => {
    try {
      const response = await fetch(`${API_BASE_URL}/notes/${id}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        showMessage('Database note deleted successfully!');
        fetchAllNotes();
      } else {
        const result = await response.json();
        showMessage('Error: ' + result.error);
      }
    } catch (error) {
      showMessage('Error deleting note: ' + error.message);
    }
  };

  const showMessage = (msg) => {
    setMessage(msg);
    setTimeout(() => setMessage(''), 5000);
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1>Notes Application</h1>
      </header>

      <main className="app-main">
        {message && (
          <div className="message">
            {message}
          </div>
        )}

        {/* Note Form */}
        <section className="note-form">
          <h2>Create New Note</h2>
          <div className="form-group">
            <label htmlFor="title">Title:</label>
            <input
              type="text"
              id="title"
              name="title"
              value={formData.title}
              onChange={handleInputChange}
              placeholder="Enter note title"
            />
          </div>
          <div className="form-group">
            <label htmlFor="content">Content:</label>
            <textarea
              id="content"
              name="content"
              value={formData.content}
              onChange={handleInputChange}
              placeholder="Enter note content"
              rows="4"
            />
          </div>
          <div className="form-actions">
            <button 
              type="button" 
              onClick={saveAsDraft} 
              disabled={loading}
              className="btn btn-draft"
            >
              {loading ? 'Saving...' : 'Save as Draft'}
            </button>
            <button 
              type="button" 
              onClick={submitNote} 
              disabled={loading}
              className="btn btn-submit"
            >
              {loading ? 'Submitting...' : 'Submit'}
            </button>
          </div>
        </section>

        {/* Notes Display */}
        <div className="notes-container">
          {/* Temporary Notes */}
          <section className="notes-section">
            <h2>Temporary Notes (Drafts)</h2>
            {loading ? (
              <p>Loading...</p>
            ) : notes.temp.length === 0 ? (
              <p className="no-notes">No temporary notes found</p>
            ) : (
              <div className="notes-list">
                {notes.temp.map((note) => (
                  <div key={note.id} className="note-card">
                    <h3>{note.title}</h3>
                    <p>{note.content}</p>
                    <div className="note-meta">
                      <span>ID: {note.id}</span>
                      <button 
                        onClick={() => deleteTempNote(note.id)}
                        className="btn btn-delete"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>

          {/* Database Notes */}
          <section className="notes-section">
            <h2>Database Notes (Submitted)</h2>
            {loading ? (
              <p>Loading...</p>
            ) : notes.db.length === 0 ? (
              <p className="no-notes">No database notes found</p>
            ) : (
              <div className="notes-list">
                {notes.db.map((note) => (
                  <div key={note.id} className="note-card">
                    <h3>{note.title}</h3>
                    <p>{note.content}</p>
                    <div className="note-meta">
                      <span>ID: {note.id}</span>
                      <button 
                        onClick={() => deleteDbNote(note.id)}
                        className="btn btn-delete"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>
        </div>
      </main>
    </div>
  );
}

export default App;