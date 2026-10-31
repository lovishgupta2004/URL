import { useState } from 'react'
import './App.css'

function App() {
  const [originalUrl, setOriginalUrl] = useState('')
  const [customCode, setCustomCode] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = (e) => {
    e.preventDefault()

  setError('')

  if (!originalUrl.trim()) {
    setError('Please enter a URL')
    return
  }

  console.log('Original URL:', originalUrl)
  console.log('Custom Code:', customCode)

  alert('URL is valid! Ready to send it to the backend.')
  }

  return (
    <div className="app">
      <div className="container">
        <h1>URL Shortener</h1>

        <p>Shorten your long URLs in seconds.</p>

        <form onSubmit={handleSubmit} noValidate>
          <label>Long URL</label>

          <input
            type="url"
            placeholder="https://example.com/very-long-url"
            value={originalUrl}
            onChange={(e) => setOriginalUrl(e.target.value)}
          />

          <label>Custom short name (optional)</label>

          <input
            type="text"
            placeholder="my-custom-link"
            value={customCode}
            onChange={(e) => setCustomCode(e.target.value)}
          />

          {/* Error will appear only when error has a value */}
          {error && <h2>ERROR: {error}</h2>}

          <button type="submit">
            Shorten URL
          </button>
        </form>
      </div>
    </div>
  )
}

export default App