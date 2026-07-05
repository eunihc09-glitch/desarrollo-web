import json
import subprocess
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BASE = 'http://127.0.0.1:8000'


def request(path, method='GET', payload=None, token=None):
    data = None if payload is None else json.dumps(payload).encode('utf-8')
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    req = urllib.request.Request(BASE + path, data=data, headers=headers, method=method)
    with urllib.request.urlopen(req, timeout=8) as response:
        return response.status, json.loads(response.read().decode('utf-8'))


def main():
    process = subprocess.Popen('mvn compile exec:java', shell=True, cwd=ROOT, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    try:
        for _ in range(150):
            try:
                status, body = request('/api/health')
                if status == 200 and body['status'] == 'ok':
                    break
            except Exception:
                time.sleep(0.2)
        else:
            raise RuntimeError('El servidor no inició correctamente.')
        _, login = request('/api/auth/login', 'POST', {'email': 'demo@cybershield.ai', 'password': 'demo1234'})
        token = login['token']
        _, dashboard = request('/api/dashboard', token=token)
        assert dashboard['dashboard']['security_level'] >= 0
        _, alerts = request('/api/alerts?severity=alta', token=token)
        assert len(alerts['alerts']) >= 1
        first_alert = alerts['alerts'][0]['id']
        _, attended = request(f'/api/alerts/{first_alert}/attend', 'PATCH', {}, token)
        assert attended['alert']['status'] == 'Atendida'
        _, incident = request('/api/incidents', 'POST', {'type': 'Phishing', 'incident_date': '2026-06-21', 'severity': 'media', 'description': 'Correo sospechoso reportado por usuario interno.', 'responsible': 'Carlos Martínez'}, token)
        assert incident['incident']['id']
        _, report = request('/api/reports', 'POST', {'report_type': 'Ejecutivo', 'date_range': '01/06/2026 - 21/06/2026'}, token)
        assert report['report']['name'].endswith('.pdf')
        _, validation = request('/api/validations', 'POST', {'module': 'Persistencia en SQLite'}, token)
        assert validation['validation']['id']
        print('Pruebas smoke completadas correctamente.')
    finally:
        process.terminate()
        try:
            process.wait(timeout=3)
        except subprocess.TimeoutExpired:
            process.kill()


if __name__ == '__main__':
    main()
